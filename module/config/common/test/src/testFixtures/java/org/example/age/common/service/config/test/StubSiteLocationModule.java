package org.example.age.common.service.config.test;

import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;
import org.example.age.common.service.config.SiteLocation;

/** Dagger module that publishes a binding for {@link SiteLocation}. */
@Module
public interface StubSiteLocationModule {

    @Provides
    @Singleton
    static SiteLocation provideSiteLocation() {
        return TestLocations.stubSite();
    }
}
