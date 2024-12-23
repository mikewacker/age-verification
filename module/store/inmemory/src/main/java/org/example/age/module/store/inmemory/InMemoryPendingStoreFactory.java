package org.example.age.module.store.inmemory;

import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.example.age.service.store.PendingStore;
import org.example.age.service.store.PendingStoreFactory;

/** Factory that creates {@link InMemoryPendingStore}'s. */
@Singleton
final class InMemoryPendingStoreFactory implements PendingStoreFactory {

    private final Map<String, InMemoryPendingStore<?>> stores = new ConcurrentHashMap<>();

    @Inject
    public InMemoryPendingStoreFactory() {}

    @Override
    @SuppressWarnings("unchecked")
    public <V> PendingStore<V> getOrCreate(String name, TypeReference<V> valueTypeRef) {
        InMemoryPendingStore<?> store = stores.computeIfAbsent(name, n -> new InMemoryPendingStore<>(valueTypeRef));
        checkValueTypeConsistent(name, store, valueTypeRef.getType());
        return (PendingStore<V>) store;
    }

    /** Checks that the value type is consistent for a single {@link PendingStore}. */
    private void checkValueTypeConsistent(String name, InMemoryPendingStore<?> store, Type valueType) {
        if (!store.getValueType().equals(valueType)) {
            String message = String.format(
                    "inconsistent value type for %s\n  create: %s\n  get: %s", name, store.getValueType(), valueType);
            throw new IllegalArgumentException(message);
        }
    }
}
