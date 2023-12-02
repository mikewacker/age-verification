package org.example.age.common.service.store;

import com.fasterxml.jackson.core.type.TypeReference;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import org.example.age.api.JsonSerializer;

/** Factory that creates {@link InMemoryPendingStore}'s. */
@Singleton
final class InMemoryPendingStoreFactory implements PendingStoreFactory {

    private final JsonSerializer serializer;

    private final Map<String, InMemoryPendingStore<?>> stores = new ConcurrentHashMap<>();

    @Inject
    public InMemoryPendingStoreFactory(@Named("service") JsonSerializer serializer) {
        this.serializer = serializer;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V> PendingStore<V> getOrCreate(String name, TypeReference<V> valueTypeRef) {
        InMemoryPendingStore<?> store =
                stores.computeIfAbsent(name, n -> new InMemoryPendingStore<>(serializer, valueTypeRef));
        checkValueTypeConsistent(store, valueTypeRef.getType());
        return (PendingStore<V>) store;
    }

    /** Checks that the value type is consistent for a single {@link PendingStore}. */
    private void checkValueTypeConsistent(InMemoryPendingStore<?> store, Type valueType) {
        if (!store.getValueType().equals(valueType)) {
            String message = String.format("inconsistent value type:\n%s\n%s", store.getValueType(), valueType);
            throw new IllegalArgumentException(message);
        }
    }
}
