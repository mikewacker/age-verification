package org.example.age.service.testing.store;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.InternalServerErrorException;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import org.example.age.service.module.store.PendingStore;

/** Fake, in-memory implementation of {@link PendingStore}. Key-value pairs do not expire. */
final class FakePendingStore<V> implements PendingStore<V> {

    private final Map<String, String> store = new HashMap<>();
    private final ObjectMapper mapper;
    private final Class<V> valueType;

    public FakePendingStore(ObjectMapper mapper, Class<V> valueType) {
        this.mapper = mapper;
        this.valueType = valueType;
    }

    @Override
    public CompletionStage<Void> put(String key, Object value, OffsetDateTime expiration) {
        try {
            String jsonValue = mapper.writeValueAsString(value);
            store.put(key, jsonValue);
            return CompletableFuture.completedFuture(null);
        } catch (JsonProcessingException e) {
            return CompletableFuture.failedFuture(new InternalServerErrorException());
        }
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
        try {
            V value = mapper.readValue(jsonValue, valueType);
            return CompletableFuture.completedFuture(Optional.of(value));
        } catch (IOException e) {
            return CompletableFuture.failedFuture(new InternalServerErrorException());
        }
    }
}
