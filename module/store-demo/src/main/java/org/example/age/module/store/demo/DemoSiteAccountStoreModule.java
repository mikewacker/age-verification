package org.example.age.module.store.demo;

import dagger.Binds;
import dagger.Module;
import org.example.age.service.api.store.SiteVerificationStore;

/**
 * Dagger module that binds {@link SiteVerificationStore}.
 * <p>
 * Data is not persisted.
 */
@Module
public interface DemoSiteAccountStoreModule {

    @Binds
    SiteVerificationStore bindSiteVerificationStore(DemoSiteVerificationStore impl);
}
