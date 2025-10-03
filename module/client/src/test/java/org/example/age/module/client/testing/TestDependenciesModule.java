package org.example.age.module.client.testing;

import dagger.Module;
import dagger.Provides;
import org.example.age.common.env.LiteEnv;
import org.example.age.module.client.AvsClientsConfig;
import org.example.age.module.client.SiteClientsConfig;
import org.example.age.testing.env.TestEnvModule;

/**
 * Dagger module that binds...
 * <ul>
 *     <li>{@link SiteClientsConfig}
 *     <li>{@link AvsClientsConfig}
 *     <li>{@link LiteEnv}
 * </ul>
 */
@Module(includes = TestEnvModule.class)
public interface TestDependenciesModule {

    @Provides
    static SiteClientsConfig provideSiteClientsConfig() {
        return TestConfig.siteClients();
    }

    @Provides
    static AvsClientsConfig provideAvsClientsConfig() {
        return TestConfig.avsClients();
    }
}
