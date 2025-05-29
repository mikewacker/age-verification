package org.example.age.module.common;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Supplier;

/** Implementation of {@link EnvUtils}. */
@Singleton
final class EnvUtilsImpl implements EnvUtils {

    private final LiteEnv env;

    @Inject
    public EnvUtilsImpl(LiteEnv env) {
        this.env = env;
    }

    @Override
    public <V> String serialize(V value) {
        try {
            return env.jsonMapper().writeValueAsString(value);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <V> V deserialize(String json, Class<V> valueType) {
        try {
            return env.jsonMapper().readValue(json, valueType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <V> CompletionStage<V> runAsync(Supplier<V> task) {
        return CompletableFuture.supplyAsync(task, env.worker());
    }
}
