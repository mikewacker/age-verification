package org.example.age.common.service.store;

import dagger.Binds;
import dagger.Module;
import org.example.age.api.JsonSerializer;

/**
 * Dagger module that publishes a binding for {@link PendingStoreFactory}, which creates in-memory stores.
 *
 * <p>Depends on an unbound <code>@Named("service") {@link JsonSerializer}</code>.</p>
 */
@Module
public interface InMemoryPendingStoreFactoryModule {

    @Binds
    PendingStoreFactory bindPendingStoreFactory(InMemoryPendingStoreFactory impl);
}
