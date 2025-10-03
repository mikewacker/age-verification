package org.example.age.module.crypto.demo.testing;

import dagger.Module;
import dagger.Provides;
import org.example.age.common.env.LiteEnv;
import org.example.age.module.crypto.demo.keys.AvsKeysConfig;
import org.example.age.module.crypto.demo.keys.SiteKeysConfig;
import org.example.age.testing.env.TestEnvModule;

/**
 * Dagger module that binds...
 * <ul>
 *     <li>{@link SiteKeysConfig}
 *     <li>{@link AvsKeysConfig}
 *     <li>{@link LiteEnv}
 * </ul>
 */
@Module(includes = TestEnvModule.class)
public interface TestDependenciesModule {

    @Provides
    static SiteKeysConfig provideSiteKeysConfig() {
        return TestConfig.siteKeys();
    }

    @Provides
    static AvsKeysConfig provideAvsKeyConfig() {
        return TestConfig.avsKeys();
    }
}
