package org.example.age.module.store.common.inmemory;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Thread-safe map where a key-value pair can be inserted with an optional secondary key.
 *
 * <p>Puts will be rejected if the secondary key is already linked to a different key.</p>
 */
final class BiKeyMap<K, K2, V> {

    private final Map<K, V> map = new HashMap<>();
    private final BiMap<K2, K> keyByKey2 = HashBiMap.create();

    private final Object lock = new Object();

    /** Creates a map. */
    public static <K, K2, V> BiKeyMap<K, K2, V> create() {
        return new BiKeyMap<>();
    }

    /** Gets the value for the key, if present. */
    public Optional<V> tryGet(K key) {
        V value;
        synchronized (lock) {
            value = map.get(key);
        }
        return Optional.ofNullable(value);
    }

    /**
     * Inserts a key-value pair with an optional secondary key, if the secondary key is not linked to a different key.
     *
     * <p>Returns the key that is already linked to the secondary key, if a conflict occurs.</p>
     */
    public Optional<K> tryPut(K key, Optional<K2> maybeKey2, V value) {
        synchronized (lock) {
            if (maybeKey2.isEmpty()) {
                removeSecondaryKey(key);
                map.put(key, value);
                return Optional.empty();
            }
            K2 key2 = maybeKey2.get();

            Optional<K> maybeConflictingKey = tryLinkSecondaryKey(key, key2);
            if (maybeConflictingKey.isPresent()) {
                return maybeConflictingKey;
            }

            map.put(key, value);
        }
        return Optional.empty();
    }

    /** Removes the value (and secondary key) for the key, if present. */
    public void remove(K key) {
        synchronized (lock) {
            removeSecondaryKey(key);
            map.remove(key);
        }
    }

    /**
     * Links a key to a secondary key, if the secondary key is not linked to a different key.
     *
     * <p>Returns the key that is already linked to the secondary key, if a conflict occurs.</p>
     */
    private Optional<K> tryLinkSecondaryKey(K key, K2 key2) {
        if (keyByKey2.containsKey(key2)) {
            K conflictingKey = keyByKey2.get(key2);
            return !key.equals(conflictingKey) ? Optional.of(conflictingKey) : Optional.empty();
        }

        keyByKey2.forcePut(key2, key);
        return Optional.empty();
    }

    /** Removes the link from a key to a secondary key. */
    private void removeSecondaryKey(K key) {
        keyByKey2.inverse().remove(key);
    }

    private BiKeyMap() {}
}
