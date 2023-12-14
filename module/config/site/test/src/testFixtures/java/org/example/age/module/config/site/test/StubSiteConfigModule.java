package org.example.age.module.config.site.test;

import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;
import org.example.age.module.config.site.SiteConfig;
import org.example.age.module.location.common.AvsLocation;
import org.example.age.module.location.common.test.StubAvsLocationModule;

/** Dagger module that publishes a binding for {@link SiteConfig}. */
@Module(includes = StubAvsLocationModule.class)
public interface StubSiteConfigModule {

    @Provides
    @Singleton
    static SiteConfig provideSiteConfig(AvsLocation avsLocation) {
        return TestSiteConfigs.create(avsLocation);
    }
}
