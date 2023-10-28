package org.example.age.common.store;

import dagger.Binds;
import dagger.Module;

/** Dagger module that publishes a binding for {@link PendingStoreFactory}, which creates in-memory stores. */
@Module
public interface InMemoryPendingStoreFactoryModule {

    @Binds
    PendingStoreFactory bindPendingStoreFactory(InMemoryPendingStoreFactory impl);
}
