package org.example.age.module.client.testing;

import dagger.Module;
import dagger.Provides;
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
 */
@Module(includes = TestLiteEnvModule.class)
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
