package org.example.age.module.location.common.test;

import dagger.Binds;
import dagger.Module;
import org.example.age.service.module.location.common.RefreshableAvsLocationProvider;
import org.example.age.testing.server.TestServer;

/**
 * Dagger module that publishes a binding for {@link RefreshableAvsLocationProvider},
 * which gets the location of a {@link TestServer} named {@code "avs"}.
 */
@Module
public interface TestAvsLocationModule {

    @Binds
    RefreshableAvsLocationProvider bindRefreshableAvsLocationProvider(TestAvsLocationProvider impl);
}
