package org.example.age.site.provider.userlocalizer.test;

import dagger.Binds;
import dagger.Module;
import org.example.age.site.spi.SiteVerifiedUserLocalizer;

/**
 * Dagger module that binds {@link SiteVerifiedUserLocalizer}.
 * <p>
 * Uses a seeded key.
 */
@Module
public abstract class TestSiteUserLocalizerModule {

    @Binds
    abstract SiteVerifiedUserLocalizer bindSiteVerifiedUserLocalizer(FakeSiteVerifiedUserLocalizer impl);

    TestSiteUserLocalizerModule() {}
}
