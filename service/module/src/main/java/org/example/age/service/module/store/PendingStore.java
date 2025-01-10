package org.example.age.service.module.store;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

/**
 * Ephemeral key-value store where a key-value pair is associated with a pending action that expires.
 * <p>
 * The value type should be serializable as JSON.
 */
public interface PendingStore<V> {

    /** Inserts a key-value pair that expires. */
    CompletionStage<Void> put(String key, V value, OffsetDateTime expiration);

    /** Gets the value for the key, if present. */
    CompletionStage<Optional<V>> tryGet(String key);

    /** Removes and returns the value for the key, if present. */
    CompletionStage<Optional<V>> tryRemove(String key);
}
