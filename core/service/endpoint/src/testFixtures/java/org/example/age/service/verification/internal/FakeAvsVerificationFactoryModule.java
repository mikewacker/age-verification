package org.example.age.service.verification.internal;

import dagger.Binds;
import dagger.Module;
import org.example.age.service.crypto.internal.AgeCertificateSignerModule;
import org.example.age.service.crypto.internal.AuthMatchDataEncryptorModule;
import org.example.age.service.key.RefreshableKeyProvider;
import org.example.age.service.store.VerificationStore;

/**
 * Dagger module that publishes a binding for {@link FakeAvsVerificationFactory}.
 *
 * <p>Depends on an unbound...</p>
 * <ul>
 *     <li>{@link VerificationStore}</li>
 *     <li>{@link RefreshableKeyProvider}</li>
 * </ul>
 */
@Module(includes = {AgeCertificateSignerModule.class, AuthMatchDataEncryptorModule.class})
public interface FakeAvsVerificationFactoryModule {

    @Binds
    FakeAvsVerificationFactory bindFakeAvsVerificationFactory(FakeAvsVerificationFactoryImpl impl);
}
