package org.example.age.module.store.demo;

import dagger.Binds;
import dagger.Module;
import org.example.age.service.module.store.SiteVerificationStore;

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
