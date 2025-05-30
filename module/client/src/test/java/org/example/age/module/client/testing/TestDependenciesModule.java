package org.example.age.module.client.testing;

import dagger.Module;
import dagger.Provides;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import org.example.age.module.client.AvsClientsConfig;
import org.example.age.module.client.SiteClientsConfig;
import org.example.age.module.common.LiteEnv;
import org.example.age.module.common.testing.TestLiteEnvModule;

/**
 * Dagger module that binds...
 * <ul>
 *     <li>{@link SiteClientsConfig}
 *     <li>{@link AvsClientsConfig}
 *     <li>{@link LiteEnv}
 * </ul>
 * <p>
 * Depends on an unbound <code>@Named("port") int</code>.
 */
@Module(includes = TestLiteEnvModule.class)
public interface TestDependenciesModule {

    @Provides
    @Singleton
    static SiteClientsConfig provideSiteClientsConfig(@Named("port") int port) {
        return TestConfig.createSiteClients(port);
    }

    @Provides
    @Singleton
    static AvsClientsConfig provideAvsClientsConfig(@Named("port") int port) {
        return TestConfig.createAvsClients(port);
    }
}
