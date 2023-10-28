package org.example.age.common.base.store;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.xnio.XnioExecutor;

/** In-memory {@link PendingStore}. */
final class InMemoryPendingStore<K, V> implements PendingStore<K, V> {

    private final BiMap<K, Holder<V>> store = HashBiMap.create();
    private final Map<K, XnioExecutor.Key> expirationKeys = new HashMap<>();

    private final Object lock = new Object();

    @Override
    public void put(K key, V value, long expiration, XnioExecutor executor) {
        // Check that the value is not already expired.
        long now = System.currentTimeMillis() / 1000;
        long expiresIn = expiration - now;
        if (expiresIn <= 0) {
            return;
        }

        synchronized (lock) {
            // Remove the old expiration task, if present.
            Optional<XnioExecutor.Key> maybeExpirationKey = Optional.ofNullable(expirationKeys.get(key));
            maybeExpirationKey.ifPresent(XnioExecutor.Key::remove);

            // Insert the value and an expiration task.
            Holder holder = new Holder(value);
            XnioExecutor.Key expirationKey = executor.executeAfter(() -> expire(holder), expiresIn, TimeUnit.SECONDS);
            store.put(key, holder);
            expirationKeys.put(key, expirationKey);
        }
    }

    @Override
    public Optional<V> tryGet(K key) {
        Optional<Holder<V>> maybeHolder;
        synchronized (lock) {
            maybeHolder = Optional.ofNullable(store.get(key));
        }
        return maybeHolder.isPresent() ? Optional.of(maybeHolder.get().v) : Optional.empty();
    }

    @Override
    public Optional<V> tryRemove(K key) {
        synchronized (lock) {
            Optional<XnioExecutor.Key> maybeExpirationKey = Optional.ofNullable(expirationKeys.remove(key));
            if (maybeExpirationKey.isEmpty()) {
                return Optional.empty();
            }

            XnioExecutor.Key expirationKey = maybeExpirationKey.get();
            expirationKey.remove();
            return Optional.of(store.remove(key).v);
        }
    }

    /** Removes an expired value. */
    private void expire(Holder<V> holder) {
        synchronized (lock) {
            Optional<K> maybeKey = Optional.ofNullable(store.inverse().remove(holder));
            if (maybeKey.isEmpty()) {
                return;
            }

            K key = maybeKey.get();
            expirationKeys.remove(key);
        }
    }

    /** Value holder that ensures that {@link Object#equals(Object)} behaves like {@code ==}. */
    private static final class Holder<V> {

        public final V v;

        public Holder(V v) {
            this.v = v;
        }
    }
}
