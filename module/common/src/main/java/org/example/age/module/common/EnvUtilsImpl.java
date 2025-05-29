package org.example.age.module.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;

/** Implementation of {@link EnvUtils}. */
@Singleton
final class EnvUtilsImpl implements EnvUtils {

    private final ObjectMapper mapper;
    private final ExecutorService worker;

    @Inject
    public EnvUtilsImpl(ObjectMapper mapper, @Named("worker") ExecutorService worker) {
        this.mapper = mapper;
        this.worker = worker;
    }

    @Override
    public <V> String serialize(V value) {
        try {
            return mapper.writeValueAsString(value);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <V> V deserialize(String json, Class<V> valueType) {
        try {
            return mapper.readValue(json, valueType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <V> CompletionStage<V> runAsync(Supplier<V> task) {
        return CompletableFuture.supplyAsync(task, worker);
    }
}
