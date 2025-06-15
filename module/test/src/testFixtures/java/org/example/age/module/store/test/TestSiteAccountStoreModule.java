package org.example.age.module.store.test;

import dagger.Binds;
import dagger.Module;
import org.example.age.service.module.store.SiteVerificationStore;

/**
 * Dagger modules that binds {@link SiteVerificationStore}.
 * <p>
 * It does not check for duplicate or expired verifications,
 * though a duplicate verification can be triggered if the account ID is "duplicate".
 */
@Module
public interface TestSiteAccountStoreModule {

    @Binds
    SiteVerificationStore bindSiteVerificationStore(FakeSiteVerificationStore impl);
}
