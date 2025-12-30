package org.example.age.common.provider.pendingstore.redis;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.example.age.common.env.JsonMapper;
import org.example.age.common.env.Worker;
import org.example.age.common.spi.PendingStore;
import org.example.age.common.spi.PendingStoreRepository;
import redis.clients.jedis.RedisClient;

/** Implementation of {@link PendingStoreRepository} that is backed by Redis. */
@Singleton
final class RedisPendingStoreRepository implements PendingStoreRepository {

    private final RedisClient client;
    private final JsonMapper mapper;
    private final Worker worker;

    private final Map<String, PendingStore<?>> stores = Collections.synchronizedMap(new HashMap<>());

    @Inject
    public RedisPendingStoreRepository(RedisClient client, JsonMapper mapper, Worker worker) {
        this.client = client;
        this.mapper = mapper;
        this.worker = worker;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <V> PendingStore<V> get(String name, Class<V> valueType) {
        return (PendingStore<V>) stores.computeIfAbsent(name, n -> create(n, valueType));
    }

    /** Creates a pending store. */
    private <V> PendingStore<V> create(String name, Class<V> valueType) {
        return new RedisPendingStore<>(client, name, valueType, mapper, worker);
    }
}
