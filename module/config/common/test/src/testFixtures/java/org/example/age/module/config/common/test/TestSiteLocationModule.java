package org.example.age.module.config.common.test;

import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import org.example.age.module.config.common.SiteLocation;
import org.example.age.testing.server.TestServer;

/**
 * Dagger module that publishes a binding for {@link SiteLocation}.
 *
 * <p>Depends on an unbound <code>@Named("site") {@link TestServer}&lt;?&gt;</code>.</p>
 */
@Module
public interface TestSiteLocationModule {

    @Provides
    static SiteLocation provideSiteLocation(@Named("site") TestServer<?> siteServer) {
        return TestLocations.site(siteServer);
    }
}