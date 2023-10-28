package org.example.age.common.store;

import java.util.Optional;
import org.xnio.XnioExecutor;

/** Ephemeral key-value store where a key-value pair is associated with a pending action that expires. */
public interface PendingStore<K, V> {

    /** Inserts a key-value pair with an expiration timestamp (in seconds). */
    void put(K key, V value, long expiration, XnioExecutor executor);

    /** Gets the value for the provided key, if present. */
    Optional<V> tryGet(K key);

    /** Removes and returns the value for the provided key, if present. */
    Optional<V> tryRemove(K key);
}
