package org.example.age.module.crypto.demo.testing;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;
import jakarta.inject.Singleton;
import org.example.age.api.crypto.SecureId;
import org.example.age.module.crypto.demo.AvsKeysConfig;
import org.example.age.module.crypto.demo.SiteKeysConfig;
import org.example.age.testing.TestObjectMapperModule;

/**
 * Dagger module that binds...
 * <ul>
 *     <li>{@link SiteKeysConfig}
 *     <li>{@link AvsKeysConfig}
 *     <li>{@link ObjectMapper}
 * </ul>
 */
@Module(includes = TestObjectMapperModule.class)
public interface TestDependenciesModule {

    @Provides
    @Singleton
    static SiteKeysConfig provideSiteKeysConfig() {
        return SiteKeysConfig.builder()
                .signing(ConfigKeyPair.publicKey())
                .localization(SecureId.generate())
                .build();
    }

    @Provides
    @Singleton
    static AvsKeysConfig provideAvsKeyConfig() {
        return AvsKeysConfig.builder()
                .signing(ConfigKeyPair.privateKey())
                .putLocalization("site", SecureId.generate())
                .build();
    }
}
