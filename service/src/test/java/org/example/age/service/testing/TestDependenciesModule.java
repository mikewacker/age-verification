package org.example.age.service.testing;

import dagger.Module;
import dagger.Provides;
import org.example.age.service.AvsServiceConfig;
import org.example.age.service.SiteServiceConfig;
import org.example.age.service.module.crypto.AgeCertificateSigner;
import org.example.age.service.module.crypto.AgeCertificateVerifier;
import org.example.age.service.module.crypto.AvsVerifiedUserLocalizer;
import org.example.age.service.module.crypto.SiteVerifiedUserLocalizer;
import org.example.age.service.module.request.AccountIdContext;
import org.example.age.service.module.store.AvsVerifiedUserStore;
import org.example.age.service.module.store.PendingStoreRepository;
import org.example.age.service.module.store.SiteVerificationStore;
import org.example.age.service.testing.crypto.TestCryptoModule;
import org.example.age.service.testing.request.TestAccountId;
import org.example.age.service.testing.request.TestRequestModule;
import org.example.age.service.testing.store.TestStoreModule;
import org.example.age.testing.TestObjectMapper;

/**
 * Dagger module that binds...
 * <ul>
 *     <li>{@link AccountIdContext}
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
@Module(
        includes = {
            TestRequestModule.class,
            TestStoreModule.class,
            TestCryptoModule.class,
            TestObjectMapper.Module.class
        })
public interface TestDependenciesModule {

    @Provides
    static SiteServiceConfig provideSiteServiceConfig() {
        return TestConfig.site();
    }

    @Provides
    static AvsServiceConfig provideAvsServiceConfig() {
        return TestConfig.avs();
    }
}
