package org.example.age.module.store.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.io.IOException;
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

    /** Gets a Redis key that uses a hash tag. */
    public String getTaggedRedisKey(String prefix, String taggedKey, String untaggedKey) {
        return String.format("{%s:%s}:%s", prefix, taggedKey, untaggedKey);
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
