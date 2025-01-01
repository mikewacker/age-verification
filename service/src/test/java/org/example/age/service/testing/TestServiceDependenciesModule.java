package org.example.age.service.testing;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;
import jakarta.inject.Singleton;
import java.time.Duration;
import org.example.age.service.AvsServiceConfig;
import org.example.age.service.SiteServiceConfig;
import org.example.age.service.api.crypto.AgeCertificateSigner;
import org.example.age.service.api.crypto.AgeCertificateVerifier;
import org.example.age.service.api.crypto.AvsVerifiedUserLocalizer;
import org.example.age.service.api.crypto.SiteVerifiedUserLocalizer;
import org.example.age.service.api.request.AccountIdExtractor;
import org.example.age.service.api.store.AvsVerifiedUserStore;
import org.example.age.service.api.store.PendingStoreRepository;
import org.example.age.service.api.store.SiteVerificationStore;
import org.example.age.service.testing.crypto.TestCryptoModule;
import org.example.age.service.testing.request.TestAccountId;
import org.example.age.service.testing.request.TestRequestModule;
import org.example.age.service.testing.store.TestStoreModule;
import org.example.age.testing.TestObjectMapper;

/**
 * Dagger module that binds...
 * <ul>
 *     <li>{@link AccountIdExtractor}
 *     <li>{@link SiteVerificationStore}
 *     <li>{@link AvsVerifiedUserStore}
 *     <li>{@link PendingStoreRepository}
 *     <li>{@link AgeCertificateVerifier}
 *     <li>{@link AgeCertificateSigner}
 *     <li>{@link SiteVerifiedUserLocalizer}
 *     <li>{@link AvsVerifiedUserLocalizer}
 *     <li>{@link SiteServiceConfig}
 *     <li>{@link AvsServiceConfig}
 * </ul>
 * <p>
 * It also binds {@link TestAccountId}.
 */
@Module(includes = {TestRequestModule.class, TestStoreModule.class, TestCryptoModule.class})
public interface TestServiceDependenciesModule {

    @Provides
    @Singleton
    static SiteServiceConfig provideSiteServiceConfig() {
        return SiteServiceConfig.builder()
                .id("site")
                .verifiedAccountExpiresIn(Duration.ofDays(30))
                .build();
    }

    @Provides
    @Singleton
    static AvsServiceConfig provideAvsServiceConfig() {
        return AvsServiceConfig.builder()
                .verificationRequestExpiresIn(Duration.ofMinutes(5))
                .build();
    }

    @Provides
    @Singleton
    static ObjectMapper provideObjectMapper() {
        return TestObjectMapper.get();
    }
}
