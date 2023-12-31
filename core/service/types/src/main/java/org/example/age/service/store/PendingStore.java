package org.example.age.service.store;

import io.github.mikewacker.drift.api.ScheduledExecutor;
import java.util.Optional;

/**
 * Ephemeral key-value store where a key-value pair is associated with a pending action that expires.
 *
 * <p>The value type should be serializable as JSON.</p>
 */
public interface PendingStore<V> {

    /** Inserts a key-value pair with an expiration timestamp (in seconds). */
    void put(String key, V value, long expiration, ScheduledExecutor executor);

    /** Gets the value for the key, if present. */
    Optional<V> tryGet(String key);

    /** Removes and returns the value for the key, if present. */
    Optional<V> tryRemove(String key);
}
