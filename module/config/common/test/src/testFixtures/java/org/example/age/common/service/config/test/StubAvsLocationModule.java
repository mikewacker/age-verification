package org.example.age.common.service.config.test;

import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;
import org.example.age.common.service.config.AvsLocation;

/** Dagger module that publishes a binding for {@link AvsLocation}. */
@Module
public interface StubAvsLocationModule {

    @Provides
    @Singleton
    static AvsLocation provideAvsLocation() {
        return TestLocations.stubAvs();
    }
}
