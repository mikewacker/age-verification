package org.example.age.module.store.inmemory;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import org.example.age.service.api.store.PendingStore;
import org.example.age.service.api.store.PendingStoreRepository;

/** Implementation of {@link PendingStoreRepository}. Requires sticky sessions to work in a distributed environment. */
@Singleton
final class InMemoryPendingStoreRepository implements PendingStoreRepository {

    private final Map<String, PendingStore<?>> stores = Collections.synchronizedMap(new HashMap<>());
    private final ObjectMapper mapper;
    private final ScheduledExecutorService scheduledExecutor;

    @Inject
    public InMemoryPendingStoreRepository(ObjectMapper mapper, ScheduledExecutorService scheduledExecutor) {
        this.mapper = mapper;
        this.scheduledExecutor = scheduledExecutor;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <V> PendingStore<V> get(String name, Class<V> valueType) {
        return (PendingStore<V>) stores.computeIfAbsent(name, n -> createPendingStore(valueType));
    }

    /** Creates a {@link PendingStore}. */
    private <V> PendingStore<V> createPendingStore(Class<V> valueType) {
        return new InMemoryPendingStore<>(mapper, valueType, scheduledExecutor);
    }
}
