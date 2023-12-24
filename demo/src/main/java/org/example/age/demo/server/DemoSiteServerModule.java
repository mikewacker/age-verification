package org.example.age.demo.server;

import dagger.Module;
import dagger.Provides;
import io.undertow.Undertow;
import javax.inject.Named;
import org.example.age.module.location.resource.ResourceSiteLocationModule;
import org.example.age.service.location.Location;
import org.example.age.service.location.RefreshableSiteLocationProvider;

/**
 * Dagger module that binds dependencies for {@link Undertow}.
 *
 * <p>Depends on an unbound {@code @Named("name") String}.</p>
 */
@Module(
        includes = {
            UndertowModule.class,
            ResourceSiteLocationModule.class,
            RootModule.class,
            DemoSiteServiceModule.class,
        })
interface DemoSiteServerModule {

    @Provides
    @Named("host")
    static String provideHost(RefreshableSiteLocationProvider siteLocationProvider, @Named("name") String name) {
        Location siteLocation = siteLocationProvider.getSite(name);
        return siteLocation.host();
    }

    @Provides
    @Named("port")
    static int providePort(RefreshableSiteLocationProvider siteLocationProvider, @Named("name") String name) {
        Location siteLocation = siteLocationProvider.getSite(name);
        return siteLocation.port();
    }
}
