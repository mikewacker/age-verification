package org.example.age.common.base.store;

/** Factory that creates {@link PendingStore}'s. */
public interface PendingStoreFactory {

    /** Creates a {@link PendingStore}. */
    <K, V> PendingStore<K, V> create();
}
