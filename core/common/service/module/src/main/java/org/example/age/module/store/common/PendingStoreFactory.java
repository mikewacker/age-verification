package org.example.age.module.store.common;

import com.fasterxml.jackson.core.type.TypeReference;

/** Factory that creates {@link PendingStore}'s. */
@FunctionalInterface
public interface PendingStoreFactory {

    /** Gets or creates a {@link PendingStore} that is identified by a name. */
    <V> PendingStore<V> getOrCreate(String name, TypeReference<V> valueTypeRef);
}
