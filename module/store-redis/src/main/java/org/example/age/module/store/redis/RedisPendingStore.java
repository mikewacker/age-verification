package org.example.age.module.store.redis;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import org.example.age.service.module.store.PendingStore;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import redis.clients.jedis.params.SetParams;

/** Implementation of {@link PendingStore} that is backed by Redis. */
final class RedisPendingStore<V> implements PendingStore<V> {

    private final JedisPooled client;
    private final RedisUtils utils;
    private final String redisKeyPrefix;
    private final Class<V> valueType;

    public RedisPendingStore(JedisPooled client, RedisUtils utils, String name, Class<V> valueType) {
        this.client = client;
        this.utils = utils;
        this.redisKeyPrefix = String.format("age:pending:%s", name);
        this.valueType = valueType;
    }

    @Override
    public CompletionStage<Void> put(String key, V value, OffsetDateTime expiration) {
        return utils.runAsync(() -> putSync(key, value, expiration));
    }

    @Override
    public CompletionStage<Optional<V>> tryGet(String key) {
        return utils.runAsync(() -> tryGetSync(key));
    }

    @Override
    public CompletionStage<Optional<V>> tryRemove(String key) {
        return utils.runAsync(() -> tryRemoveSync(key));
    }

    private Void putSync(String key, V value, OffsetDateTime expiration) {
        String redisKey = utils.getRedisKey(redisKeyPrefix, key);
        String json = utils.serialize(value);
        long pxAt = expiration.toInstant().toEpochMilli();
        client.set(redisKey, json, new SetParams().pxAt(pxAt));
        return null;
    }

    private Optional<V> tryGetSync(String key) {
        String redisKey = utils.getRedisKey(redisKeyPrefix, key);
        String json = client.get(redisKey);
        return (json != null) ? Optional.of(utils.deserialize(json, valueType)) : Optional.empty();
    }

    private Optional<V> tryRemoveSync(String key) {
        String redisKey = utils.getRedisKey(redisKeyPrefix, key);
        String json = del(redisKey);
        return (json != null) ? Optional.of(utils.deserialize(json, valueType)) : Optional.empty();
    }

    /** Deletes a key from Redis, returning the value. */
    private String del(String redisKey) {
        try (Pipeline pipeline = client.pipelined()) {
            Response<String> jsonResponse = pipeline.get(redisKey);
            pipeline.del(redisKey);
            pipeline.sync();
            return jsonResponse.get();
        }
    }
}
