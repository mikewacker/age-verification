package org.example.age.module.common;

import java.util.concurrent.CompletionStage;
import java.util.function.Supplier;

/** Utilities that depend on the environment. */
public interface EnvUtils {

    /** Serializes a value to JSON. */
    <V> String serialize(V value);

    /** Deserializes a value from JSON. */
    <V> V deserialize(String json, Class<V> valueType);

    /** Runs a task asynchronously on a worker thread. */
    <V> CompletionStage<V> runAsync(Supplier<V> task);
}
