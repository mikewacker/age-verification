package org.example.age.common.service.store;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Binds;
import dagger.Module;

/**
 * Dagger module that publishes a binding for {@link PendingStoreFactory}, which creates in-memory stores.
 *
 * <p>Depends on an unbound {@link ObjectMapper}.</p>
 */
@Module
public interface InMemoryPendingStoreFactoryModule {

    @Binds
    PendingStoreFactory bindPendingStoreFactory(InMemoryPendingStoreFactory impl);
}
