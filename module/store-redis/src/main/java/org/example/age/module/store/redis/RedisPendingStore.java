package org.example.age.module.store.redis;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import org.example.age.module.common.EnvUtils;
import org.example.age.service.module.store.PendingStore;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import redis.clients.jedis.params.SetParams;

/** Implementation of {@link PendingStore} that is backed by Redis. */
final class RedisPendingStore<V> implements PendingStore<V> {

    private static final String REDIS_KEY_PREFIX = "age:pending";

    private final JedisPooled client;
    private final String name;
    private final Class<V> valueType;
    private final EnvUtils utils;

    public RedisPendingStore(JedisPooled client, String name, Class<V> valueType, EnvUtils utils) {
        this.client = client;
        this.name = name;
        this.valueType = valueType;
        this.utils = utils;
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
        String redisKey = getRedisKey(key);
        String json = utils.serialize(value);
        long pxAt = expiration.toInstant().toEpochMilli();
        client.set(redisKey, json, new SetParams().pxAt(pxAt));
        return null;
    }

    private Optional<V> tryGetSync(String key) {
        String redisKey = getRedisKey(key);
        String json = client.get(redisKey);
        return (json != null) ? Optional.of(utils.deserialize(json, valueType)) : Optional.empty();
    }

    private Optional<V> tryRemoveSync(String key) {
        String redisKey = getRedisKey(key);
        String json = del(redisKey);
        return (json != null) ? Optional.of(utils.deserialize(json, valueType)) : Optional.empty();
    }

    /** Gets the Redis key for a key. */
    private String getRedisKey(String key) {
        return String.format("%s:%s:%s", REDIS_KEY_PREFIX, name, key);
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
