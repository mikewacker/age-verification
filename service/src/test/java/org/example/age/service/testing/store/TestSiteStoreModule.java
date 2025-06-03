package org.example.age.service.testing.store;

import dagger.Binds;
import dagger.Module;
import org.example.age.service.module.store.PendingStoreRepository;
import org.example.age.service.module.store.SiteVerificationStore;

/**
 * Dagger modules that binds...
 * <ul>
 *     <li>{@link SiteVerificationStore}
 *     <li>{@link PendingStoreRepository}
 * </ul>
 */
@Module
public interface TestSiteStoreModule {

    @Binds
    SiteVerificationStore bindSiteVerificationStore(FakeSiteVerificationStore impl);

    @Binds
    PendingStoreRepository bindPendingStoreRepository(FakePendingStoreRepository impl);
}
