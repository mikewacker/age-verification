package org.example.age.site.service.config.test;

import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;
import org.example.age.common.service.config.AvsLocation;
import org.example.age.common.service.config.test.StubAvsLocationModule;
import org.example.age.site.service.config.SiteConfig;

/** Dagger module that publishes a binding for {@link SiteConfig}. */
@Module(includes = StubAvsLocationModule.class)
public interface StubSiteConfigModule {

    @Provides
    @Singleton
    static SiteConfig provideSiteConfig(AvsLocation avsLocation) {
        return TestSiteConfigs.create(avsLocation);
    }
}
