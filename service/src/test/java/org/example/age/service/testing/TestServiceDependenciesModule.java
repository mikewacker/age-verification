package org.example.age.service.testing;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import jakarta.inject.Singleton;
import org.example.age.service.api.AccountIdExtractor;
import org.example.age.service.api.AgeCertificateSigner;
import org.example.age.service.api.AgeCertificateVerifier;
import org.example.age.service.api.PendingStoreRepository;

/**
 * Dagger module that binds...
 * <ul>
 *     <li>{@link AccountIdExtractor}
 *     <li>{@link PendingStoreRepository}
 *     <li>{@link AgeCertificateSigner}
 *     <li>{@link AgeCertificateVerifier}
 * </ul>
 * <p>
 * It also binds {@link TestAccountId}.
 */
@Module
interface TestServiceDependenciesModule {

    @Binds
    AccountIdExtractor bindAccountIdExtractor(FakeAccountIdExtractor impl);

    @Binds
    PendingStoreRepository bindPendingStoreRepository(FakePendingStoreRepository impl);

    @Binds
    AgeCertificateSigner bindAgeCertificateSigner(FakeAgeCertificateSignerVerifier impl);

    @Binds
    AgeCertificateVerifier bindAgeCertificateVerifier(FakeAgeCertificateSignerVerifier impl);

    @Provides
    @Singleton
    static ObjectMapper provideObjectMapper() {
        return TestObjectMapper.get();
    }
}
