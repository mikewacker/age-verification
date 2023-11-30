package org.example.age.test.common.service.data;

import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;
import org.example.age.common.service.data.AvsLocation;

/** Dagger module that publishes a binding for {@link AvsLocation}. */
@Module
public interface StubAvsLocationModule {

    @Provides
    @Singleton
    static AvsLocation provideAvsLocation() {
        return TestLocations.stubAvs();
    }
}
