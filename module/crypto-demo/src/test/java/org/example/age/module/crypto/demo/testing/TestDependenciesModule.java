package org.example.age.module.crypto.demo.testing;

import dagger.Module;
import dagger.Provides;
import org.example.age.module.common.LiteEnv;
import org.example.age.module.common.testing.TestLiteEnvModule;
import org.example.age.module.crypto.demo.keys.AvsKeysConfig;
import org.example.age.module.crypto.demo.keys.SiteKeysConfig;

/**
 * Dagger module that binds...
 * <ul>
 *     <li>{@link SiteKeysConfig}
 *     <li>{@link AvsKeysConfig}
 *     <li>{@link LiteEnv}
 * </ul>
 */
@Module(includes = TestLiteEnvModule.class)
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
