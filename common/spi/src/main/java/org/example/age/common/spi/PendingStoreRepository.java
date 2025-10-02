package org.example.age.common.spi;

/** Repository of ephemeral key-value stores. */
@FunctionalInterface
public interface PendingStoreRepository {

    /** Gets a {@link PendingStore} that is identified by name. This operation should not block. */
    <V> PendingStore<V> get(String name, Class<V> valueType);
}
