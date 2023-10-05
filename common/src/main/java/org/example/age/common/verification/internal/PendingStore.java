package org.example.age.common.verification.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.xnio.XnioExecutor;

/**
 * Key-value store where values represent a pending action that expires.
 *
 * <p>Keys are expected to be unique; no two values should have the same key.</p>
 */
public final class PendingStore<K, V> {

    private final Map<K, V> store = new HashMap<>();
    private final Map<K, XnioExecutor.Key> expirationKeys = new HashMap<>();

    private final Object lock = new Object();

    /** Creates a store. */
    public static <K, V> PendingStore<K, V> create() {
        return new PendingStore<>();
    }

    /** Inserts a value with an expiration, returning true if it was inserted. */
    public boolean put(K key, V value, long expiration, XnioExecutor executor) {
        long now = System.currentTimeMillis() / 1000;
        long expiresIn = expiration - now;
        if (expiresIn <= 0) {
            return false;
        }

        synchronized (lock) {
            Optional<V> maybeOldValue = Optional.ofNullable(store.putIfAbsent(key, value));
            if (maybeOldValue.isPresent()) {
                throw new IllegalArgumentException("duplicate key");
            }

            XnioExecutor.Key expirationKey;
            try {
                expirationKey = executor.executeAfter(() -> expire(key), expiresIn, TimeUnit.SECONDS);
            } catch (RuntimeException e) {
                store.remove(key, value);
                throw e;
            }
            expirationKeys.put(key, expirationKey);
        }
        return true;
    }

    /** Tries to remove and return a value, if it exists. */
    public Optional<V> tryRemove(K key) {
        Optional<V> maybeValue;
        Optional<XnioExecutor.Key> maybeExpirationKey;
        synchronized (lock) {
            maybeValue = Optional.ofNullable(store.remove(key));
            maybeExpirationKey = Optional.ofNullable(expirationKeys.remove(key));
        }
        maybeExpirationKey.ifPresent(XnioExecutor.Key::remove);
        return maybeValue;
    }

    /** Removes an expired value. */
    private void expire(K key) {
        synchronized (lock) {
            store.remove(key);
            expirationKeys.remove(key);
        }
    }

    private PendingStore() {}
}
