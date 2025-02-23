package org.example.age.module.store.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;

/** Utilities for working with Redis. */
@Singleton
final class RedisUtils {

    private final ObjectMapper mapper;
    private final ExecutorService worker;

    @Inject
    public RedisUtils(ObjectMapper mapper, @Named("worker") ExecutorService worker) {
        this.mapper = mapper;
        this.worker = worker;
    }

    /** Runs a task (that issues Redis commands) asynchronously on a worker thread. */
    public <V> CompletionStage<V> runAsync(Supplier<V> task) {
        return CompletableFuture.supplyAsync(task, worker);
    }

    /** Gets a Redis key. */
    public String getRedisKey(String prefix, String key) {
        return String.format("%s:%s", prefix, key);
    }

    /** Converts an expiration to a duration in seconds, rounding up. */
    public long toExpiresInSeconds(OffsetDateTime expiration) {
        Duration expiresIn = Duration.between(OffsetDateTime.now(ZoneOffset.UTC), expiration);
        expiresIn = expiresIn.plusSeconds(1).minusNanos(1).truncatedTo(ChronoUnit.SECONDS);
        return expiresIn.toSeconds();
    }

    /** Serializes a value to JSON. */
    public <V> String serialize(V value) {
        try {
            return mapper.writeValueAsString(value);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /** Deserializes a value from JSON. */
    public <V> V deserialize(String json, Class<V> valueType) {
        try {
            return mapper.readValue(json, valueType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
