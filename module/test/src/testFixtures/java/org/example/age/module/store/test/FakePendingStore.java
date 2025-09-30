package org.example.age.module.store.test;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import org.example.age.service.module.store.PendingStore;
import org.example.age.testing.util.TestObjectMapper;

/** Fake, in-memory implementation of {@link PendingStore}. Key-value pairs do not expire. */
final class FakePendingStore<V> implements PendingStore<V> {

    private final Map<String, String> store = new HashMap<>();
    private final Class<V> valueType;

    public FakePendingStore(Class<V> valueType) {
        this.valueType = valueType;
    }

    @Override
    public CompletionStage<Void> put(String key, Object value, OffsetDateTime expiration) {
        String jsonValue = TestObjectMapper.serialize(value);
        store.put(key, jsonValue);
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletionStage<Optional<V>> tryGet(String key) {
        Optional<String> maybeJsonValue = Optional.ofNullable(store.get(key));
        return toCompletedValue(maybeJsonValue);
    }

    @Override
    public CompletionStage<Optional<V>> tryRemove(String key) {
        Optional<String> maybeJsonValue = Optional.ofNullable(store.remove(key));
        return toCompletedValue(maybeJsonValue);
    }

    /** Converts a JSON value (if present) to a completed value. */
    private CompletionStage<Optional<V>> toCompletedValue(Optional<String> maybeJsonValue) {
        if (maybeJsonValue.isEmpty()) {
            return CompletableFuture.completedFuture(Optional.empty());
        }

        String jsonValue = maybeJsonValue.get();
        V value = TestObjectMapper.deserialize(jsonValue, valueType);
        return CompletableFuture.completedFuture(Optional.of(value));
    }
}
