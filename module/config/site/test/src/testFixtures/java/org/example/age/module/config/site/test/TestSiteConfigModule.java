package org.example.age.module.config.site.test;

import dagger.Module;
import dagger.Provides;
import org.example.age.module.config.common.AvsLocation;
import org.example.age.module.config.common.test.TestAvsLocationModule;
import org.example.age.module.config.site.SiteConfig;
import org.example.age.testing.server.TestServer;

/**
 * Dagger module that publishes a binding for {@link SiteConfig}.
 *
 * <p>Depends on an unbound <code>@Named("avs") {@link TestServer}&lt;?&gt;</code>.</p>
 */
@Module(includes = TestAvsLocationModule.class)
public interface TestSiteConfigModule {

    @Provides
    static SiteConfig provideSiteConfig(AvsLocation avsLocation) {
        return TestSiteConfigs.create(avsLocation);
    }
}
