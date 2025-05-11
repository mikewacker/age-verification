package org.example.age.service.module.env;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;

/** Utilities that depend on the environment. */
@Singleton
public final class EnvUtils {

    private final ObjectMapper mapper;
    private final ExecutorService worker;

    @Inject
    public EnvUtils(ObjectMapper mapper, @Named("worker") ExecutorService worker) {
        this.mapper = mapper;
        this.worker = worker;
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

    /** Runs a task asynchronously on a worker thread. */
    public <V> CompletionStage<V> runAsync(Supplier<V> task) {
        return CompletableFuture.supplyAsync(task, worker);
    }
}
