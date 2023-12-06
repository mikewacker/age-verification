package org.example.age.common.service.store.inmemory;

import dagger.Binds;
import dagger.Module;
import org.example.age.common.service.store.PendingStoreFactory;

/** Dagger module that publishes a binding for {@link PendingStoreFactory}, which creates in-memory stores. */
@Module
public interface InMemoryPendingStoreFactoryModule {

    @Binds
    PendingStoreFactory bindPendingStoreFactory(InMemoryPendingStoreFactory impl);
}
