package org.example.age.avs.provider.userlocalizer.demo;

import dagger.Binds;
import dagger.Module;
import org.example.age.site.spi.SiteVerifiedUserLocalizer;

/**
 * Dagger module that binds {@link SiteVerifiedUserLocalizer}.
 * <p>
 * Depends on an unbound {@link SiteLocalizationKeyConfig}.
 */
@Module
public abstract class DemoSiteUserLocalizerModule {

    @Binds
    abstract SiteVerifiedUserLocalizer bindSiteVerifiedUserLocalizer(DemoSiteVerifiedUserLocalizer impl);

    DemoSiteUserLocalizerModule() {}
}
