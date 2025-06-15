package org.example.age.module.store.test;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.HashMap;
import java.util.Map;
import org.example.age.service.module.store.PendingStore;
import org.example.age.service.module.store.PendingStoreRepository;

/** Fake, in-memory implementation of {@link PendingStoreRepository}. Key-value pairs do not expire. */
@Singleton
final class FakePendingStoreRepository implements PendingStoreRepository {

    private final Map<String, PendingStore<?>> stores = new HashMap<>();

    @Inject
    public FakePendingStoreRepository() {}

    @SuppressWarnings("unchecked")
    @Override
    public <V> PendingStore<V> get(String name, Class<V> valueType) {
        return (PendingStore<V>) stores.computeIfAbsent(name, n -> new FakePendingStore<>(valueType));
    }
}
