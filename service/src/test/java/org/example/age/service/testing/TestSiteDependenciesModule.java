package org.example.age.service.testing;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import jakarta.inject.Named;
import org.example.age.api.SiteApi;
import org.example.age.service.SiteServiceConfig;

/**
 * Dagger module that binds...
 * <ul>
 *     <li><code>@Named("testService") {@link SiteApi}</code>
 *     <li>{@link SiteServiceConfig}
 * </ul>
 * <p>
 * Depends on an unbound <code>@Named("service") {@link SiteApi}</code>.
 */
@Module
interface TestSiteDependenciesModule {

    @Binds
    @Named("testService")
    SiteApi bindSiteTestService(TestWrappedSiteService impl);

    @Provides
    static SiteServiceConfig provideSiteServiceConfig() {
        return TestConfig.siteService();
    }
}
