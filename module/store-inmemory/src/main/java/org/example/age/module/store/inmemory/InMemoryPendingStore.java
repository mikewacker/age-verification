package org.example.age.module.store.inmemory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.example.age.service.module.store.PendingStore;

/** Implementation of {@link PendingStore}. Requires sticky sessions to work in a distributed environment. */
final class InMemoryPendingStore<V> implements PendingStore<V> {

    private final BiMap<String, JsonHolder> store = Maps.synchronizedBiMap(HashBiMap.create());
    private final ObjectMapper mapper;
    private final Class<V> valueType;
    private final ScheduledExecutorService scheduledExecutor;

    public InMemoryPendingStore(ObjectMapper mapper, Class<V> valueType, ScheduledExecutorService scheduledExecutor) {
        this.mapper = mapper;
        this.valueType = valueType;
        this.scheduledExecutor = scheduledExecutor;
    }

    @SuppressWarnings("FutureReturnValueIgnored")
    @Override
    public CompletionStage<Void> put(String key, V value, OffsetDateTime expiration) {
        Duration expiresIn = Duration.between(OffsetDateTime.now(ZoneOffset.UTC), expiration);
        if (expiresIn.isNegative() || expiresIn.isZero()) {
            return CompletableFuture.completedFuture(null);
        }

        JsonHolder jsonHolder = serialize(value);
        store.put(key, jsonHolder);
        Runnable expirationTask = () -> store.inverse().remove(jsonHolder);
        scheduledExecutor.schedule(expirationTask, expiresIn.toMillis(), TimeUnit.MILLISECONDS);
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletionStage<Optional<V>> tryGet(String key) {
        Optional<V> maybeValue = Optional.ofNullable(store.get(key)).map(this::deserialize);
        return CompletableFuture.completedFuture(maybeValue);
    }

    @Override
    public CompletionStage<Optional<V>> tryRemove(String key) {
        Optional<V> maybeValue = Optional.ofNullable(store.remove(key)).map(this::deserialize);
        return CompletableFuture.completedFuture(maybeValue);
    }

    /** Serializes the value to JSON. */
    private JsonHolder serialize(V value) {
        try {
            return new JsonHolder(mapper.writeValueAsString(value));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /** Deserializes a value from JSON. */
    private V deserialize(JsonHolder jsonHolder) {
        try {
            return mapper.readValue(jsonHolder.value, valueType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /** Holder for a JSON value. {@link #equals(Object)} behaves like {@code ==}. */
    private static final class JsonHolder {

        public final String value;

        public JsonHolder(String value) {
            this.value = value;
        }
    }
}
