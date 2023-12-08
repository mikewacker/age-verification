package org.example.age.module.config.site.test;

import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;
import org.example.age.module.config.common.AvsLocation;
import org.example.age.module.config.common.test.StubAvsLocationModule;
import org.example.age.module.config.site.SiteConfig;

/** Dagger module that publishes a binding for {@link SiteConfig}. */
@Module(includes = StubAvsLocationModule.class)
public interface StubSiteConfigModule {

    @Provides
    @Singleton
    static SiteConfig provideSiteConfig(AvsLocation avsLocation) {
        return TestSiteConfigs.create(avsLocation);
    }
}
