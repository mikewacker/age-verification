package org.example.age.service.testing;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.HashMap;
import java.util.Map;
import org.example.age.service.api.PendingStore;
import org.example.age.service.api.PendingStoreRepository;

/** Fake, in-memory implementation of {@link PendingStoreRepository}. Key-value pairs do not expire. */
@Singleton
final class FakePendingStoreRepository implements PendingStoreRepository {

    private final Map<String, PendingStore<?>> stores = new HashMap<>();
    private final ObjectMapper mapper;

    @Inject
    public FakePendingStoreRepository(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <V> PendingStore<V> get(String name, Class<V> valueType) {
        return (PendingStore<V>) stores.computeIfAbsent(name, n -> new FakePendingStore<>(mapper, valueType));
    }
}
