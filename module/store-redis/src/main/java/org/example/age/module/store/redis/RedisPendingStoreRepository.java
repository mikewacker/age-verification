package org.example.age.module.store.redis;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.example.age.service.module.env.EnvUtils;
import org.example.age.service.module.store.PendingStore;
import org.example.age.service.module.store.PendingStoreRepository;
import redis.clients.jedis.JedisPooled;

/** Implementation of {@link PendingStoreRepository} that is backed by Redis. */
@Singleton
final class RedisPendingStoreRepository implements PendingStoreRepository {

    private final JedisPooled client;
    private final EnvUtils utils;

    private final Map<String, PendingStore<?>> stores = Collections.synchronizedMap(new HashMap<>());

    @Inject
    public RedisPendingStoreRepository(JedisPooled client, EnvUtils utils) {
        this.client = client;
        this.utils = utils;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <V> PendingStore<V> get(String name, Class<V> valueType) {
        return (PendingStore<V>) stores.computeIfAbsent(name, n -> create(n, valueType));
    }

    /** Creates a pending store. */
    private <V> PendingStore<V> create(String name, Class<V> valueType) {
        return new RedisPendingStore<>(client, name, valueType, utils);
    }
}
