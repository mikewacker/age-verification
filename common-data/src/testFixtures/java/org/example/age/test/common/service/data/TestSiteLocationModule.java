package org.example.age.test.common.service.data;

import dagger.Module;
import dagger.Provides;
import org.example.age.common.service.data.SiteLocation;
import org.example.age.testing.server.TestServer;

/**
 * Dagger module that publishes a binding for {@link SiteLocation}.
 *
 * <p>Depends on an unbound {@link TestServer}.</p>
 */
@Module
public interface TestSiteLocationModule {

    @Provides
    static SiteLocation provideSiteLocation(TestServer<?> siteServer) {
        return TestLocations.site(siteServer);
    }
}
