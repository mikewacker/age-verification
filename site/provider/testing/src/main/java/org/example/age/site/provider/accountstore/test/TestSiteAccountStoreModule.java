package org.example.age.site.provider.accountstore.test;

import dagger.Binds;
import dagger.Module;
import org.example.age.site.spi.SiteVerifiedAccountStore;

/**
 * Dagger modules that binds {@link SiteVerifiedAccountStore}.
 * <p>
 * It does not check for duplicate or expired verifications,
 * though a duplicate verification can be triggered if the account ID is "duplicate".
 */
@Module
public abstract class TestSiteAccountStoreModule {

    @Binds
    abstract SiteVerifiedAccountStore bindSiteVerifiedAccountStore(FakeSiteVerifiedAccountStore impl);

    TestSiteAccountStoreModule() {}
}
