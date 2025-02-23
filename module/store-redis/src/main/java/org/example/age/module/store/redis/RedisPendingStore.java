package org.example.age.module.store.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Named;
import java.io.IOException;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;
import org.example.age.service.module.store.PendingStore;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import redis.clients.jedis.params.SetParams;

/** Implementation of {@link PendingStore} that is backed by Redis. */
final class RedisPendingStore<V> implements PendingStore<V> {

    private final JedisPooled client;
    private final String redisKeyPrefix;
    private final Class<V> valueType;
    private final ObjectMapper mapper;
    private final ExecutorService worker;

    public RedisPendingStore(
            JedisPooled client,
            String name,
            Class<V> valueType,
            ObjectMapper mapper,
            @Named("worker") ExecutorService worker) {
        this.client = client;
        this.redisKeyPrefix = String.format("age:pending:%s", name);
        this.valueType = valueType;
        this.mapper = mapper;
        this.worker = worker;
    }

    @Override
    public CompletionStage<Void> put(String key, V value, OffsetDateTime expiration) {
        return CompletableFuture.supplyAsync(() -> putSync(key, value, expiration), worker);
    }

    @Override
    public CompletionStage<Optional<V>> tryGet(String key) {
        return CompletableFuture.supplyAsync(() -> tryGetSync(key), worker);
    }

    @Override
    public CompletionStage<Optional<V>> tryRemove(String key) {
        return CompletableFuture.supplyAsync(() -> tryRemoveSync(key), worker);
    }

    private Void putSync(String key, V value, OffsetDateTime expiration) {
        long expiresInS = toExpiresInSeconds(expiration);
        if (expiresInS <= 0) {
            return null;
        }

        String redisKey = getRedisKey(key);
        String json = serialize(value);
        client.set(redisKey, json, new SetParams().ex(expiresInS));
        return null;
    }

    private Optional<V> tryGetSync(String key) {
        String redisKey = getRedisKey(key);
        String json = client.get(redisKey);
        return (json != null) ? Optional.of(deserialize(json)) : Optional.empty();
    }

    private Optional<V> tryRemoveSync(String key) {
        String redisKey = getRedisKey(key);
        String json = del(redisKey);
        return (json != null) ? Optional.of(deserialize(json)) : Optional.empty();
    }

    /** Gets the key for Redis. */
    private String getRedisKey(String key) {
        return String.format("%s:%s", redisKeyPrefix, key);
    }

    /** Converts an expiration to a duration in seconds, rounding up. */
    private long toExpiresInSeconds(OffsetDateTime expiration) {
        Duration expiresIn = Duration.between(OffsetDateTime.now(ZoneOffset.UTC), expiration);
        expiresIn = expiresIn.plusSeconds(1).minusNanos(1).truncatedTo(ChronoUnit.SECONDS);
        return expiresIn.toSeconds();
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

    /** Serializes the value to JSON. */
    private String serialize(V value) {
        try {
            return mapper.writeValueAsString(value);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /** Deserializes a value from JSON. */
    private V deserialize(String json) {
        try {
            return mapper.readValue(json, valueType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
