package org.example.age.common.avs.store;

import dagger.Binds;
import dagger.Module;

/** Dagger module that publishes a binding for {@link SiteConfigStore}, which uses an in-memory store. */
@Module
public interface InMemorySiteConfigStoreModule {

    @Binds
    SiteConfigStore bindSiteConfigStore(InMemorySiteConfigStore impl);
}
