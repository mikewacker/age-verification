package org.example.age.common.service.config.test;

import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import org.example.age.common.service.config.AvsLocation;
import org.example.age.testing.server.TestServer;

/**
 * Dagger module that publishes a binding for {@link AvsLocation}.
 *
 * <p>Depends on an unbound <code>@Named("avs") {@link TestServer}&lt;?&gt;</code>.</p>
 */
@Module
public interface TestAvsLocationModule {

    @Provides
    static AvsLocation provideAvsLocation(@Named("avs") TestServer<?> avsServer) {
        return TestLocations.avs(avsServer);
    }
}
