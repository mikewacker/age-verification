package org.example.age.common.avs.store;

import dagger.Binds;
import dagger.Module;

/** Dagger module that publishes a binding for {@link RegisteredSiteConfigStore}, which uses an in-memory store. */
@Module
public interface InMemoryRegisteredSiteConfigStoreModule {

    @Binds
    RegisteredSiteConfigStore bindSiteConfigStore(InMemoryRegisteredSiteConfigStore impl);
}
