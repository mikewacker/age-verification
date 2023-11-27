package org.example.age.test.common.service.data;

import dagger.Module;
import dagger.Provides;
import org.example.age.common.service.data.AvsLocation;
import org.example.age.testing.server.TestServer;

/**
 * Dagger module that publishes a binding for {@link AvsLocation}.
 *
 * <p>Depends on an unbound {@link TestServer}.</p>
 */
@Module
public interface TestAvsLocationModule {

    @Provides
    static AvsLocation provideAvsLocation(TestServer<?> avsServer) {
        return TestLocations.avs(avsServer);
    }
}
