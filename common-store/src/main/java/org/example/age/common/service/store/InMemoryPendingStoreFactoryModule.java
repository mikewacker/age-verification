package org.example.age.common.service.store;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Binds;
import dagger.Module;
import org.example.age.infra.api.data.JsonSerializerModule;

/**
 * Dagger module that publishes a binding for {@link PendingStoreFactory}, which creates in-memory stores.
 *
 * <p>Depends on an unbound {@link ObjectMapper}.</p>
 */
@Module(includes = JsonSerializerModule.class)
public interface InMemoryPendingStoreFactoryModule {

    @Binds
    PendingStoreFactory bindPendingStoreFactory(InMemoryPendingStoreFactory impl);
}
