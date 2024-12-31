package org.example.age.service.testing;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import jakarta.inject.Singleton;
import java.time.Duration;
import org.example.age.service.SiteServiceConfig;
import org.example.age.service.api.AccountIdExtractor;
import org.example.age.service.api.AgeCertificateSigner;
import org.example.age.service.api.AgeCertificateVerifier;
import org.example.age.service.api.PendingStoreRepository;
import org.example.age.service.api.SiteLocalizationKeyStore;
import org.example.age.service.api.SiteVerificationStore;

/**
 * Dagger module that binds...
 * <ul>
 *     <li>{@link AccountIdExtractor}
 *     <li>{@link SiteVerificationStore}
 *     <li>{@link PendingStoreRepository}
 *     <li>{@link AgeCertificateVerifier}
 *     <li>{@link SiteLocalizationKeyStore}
 *     <li>{@link SiteServiceConfig}
 * </ul>
 * <p>
 * It also binds...
 * <ul>
 *     <li>{@link TestAccountId}
 *     <li>{@link AgeCertificateSigner}
 * </ul>
 */
@Module(includes = TestServiceDependenciesModule.class)
public interface TestSiteServiceDependenciesModule {

    @Binds
    SiteVerificationStore bindSiteVerificationStore(FakeSiteVerificationStore impl);

    @Binds
    SiteLocalizationKeyStore bindSiteLocalizationKeyStore(FakeSiteLocalizationKeyStore impl);

    @Provides
    @Singleton
    static SiteServiceConfig provideSiteServiceConfig() {
        return SiteServiceConfig.builder()
                .id("site")
                .verifiedAccountExpiresIn(Duration.ofDays(30))
                .build();
    }
}
