package org.example.age.common.base.store;

import javax.inject.Inject;
import javax.inject.Singleton;

/** Factory that creates {@link InMemoryPendingStore}'s. */
@Singleton
final class InMemoryPendingStoreFactory implements PendingStoreFactory {

    @Inject
    public InMemoryPendingStoreFactory() {}

    @Override
    public <K, V> PendingStore<K, V> create() {
        return new InMemoryPendingStore<>();
    }
}
