package org.example.age.module.location.test;

import dagger.Binds;
import dagger.Module;
import io.github.mikewacker.drift.testing.server.TestServer;
import org.example.age.service.location.RefreshableSiteLocationProvider;

/**
 * Dagger module that publishes a binding for {@link RefreshableSiteLocationProvider},
 * which gets the location of a {@link TestServer} named {@code "site"} for a site with ID {@code "Site"}.
 */
@Module
public interface TestSiteLocationModule {

    @Binds
    RefreshableSiteLocationProvider bindRefreshableSiteLocationProvider(TestLocationProvider impl);
}
