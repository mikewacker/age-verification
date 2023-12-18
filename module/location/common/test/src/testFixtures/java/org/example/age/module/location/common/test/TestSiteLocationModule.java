package org.example.age.module.location.common.test;

import dagger.Binds;
import dagger.Module;
import org.example.age.service.location.common.RefreshableSiteLocationProvider;
import org.example.age.testing.server.TestServer;

/**
 * Dagger module that publishes a binding for {@link RefreshableSiteLocationProvider},
 * which gets the location of a {@link TestServer} named {@code "site"} for a site with ID {@code "Site"}.
 */
@Module
public interface TestSiteLocationModule {

    @Binds
    RefreshableSiteLocationProvider bindRefreshableSiteLocationProvider(TestLocationProvider impl);
}
