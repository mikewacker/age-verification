package org.example.age.module.crypto.demo.testing;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;
import org.example.age.module.crypto.demo.AvsKeysConfig;
import org.example.age.module.crypto.demo.SiteKeysConfig;
import org.example.age.testing.TestEnvModule;

/**
 * Dagger module that binds...
 * <ul>
 *     <li>{@link SiteKeysConfig}
 *     <li>{@link AvsKeysConfig}
 *     <li>{@link ObjectMapper}
 * </ul>
 */
@Module(includes = TestEnvModule.class)
public interface TestDependenciesModule {

    @Provides
    static SiteKeysConfig provideSiteKeysConfig() {
        return TestConfig.site();
    }

    @Provides
    static AvsKeysConfig provideAvsKeyConfig() {
        return TestConfig.avs();
    }
}
