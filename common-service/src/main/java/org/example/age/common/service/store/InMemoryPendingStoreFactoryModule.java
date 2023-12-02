package org.example.age.common.service.store;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Binds;
import dagger.Module;
import org.example.age.infra.service.ServiceJsonSerializerModule;

/**
 * Dagger module that publishes a binding for {@link PendingStoreFactory}, which creates in-memory stores.
 *
 * <p>Depends on an unbound <code>@Named("service") {@link ObjectMapper}</code>.</p>
 */
@Module(includes = ServiceJsonSerializerModule.class)
public interface InMemoryPendingStoreFactoryModule {

    @Binds
    PendingStoreFactory bindPendingStoreFactory(InMemoryPendingStoreFactory impl);
}
