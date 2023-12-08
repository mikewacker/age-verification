package org.example.age.avs.service.verification.internal;

import dagger.Binds;
import dagger.Module;
import org.example.age.common.service.crypto.internal.AgeCertificateSignerModule;
import org.example.age.common.service.crypto.internal.AuthMatchDataEncryptorModule;
import org.example.age.common.service.key.RefreshableKeyProvider;

/**
 * Dagger module that publishes a binding for {@link FakeAvsVerificationFactory}.
 *
 * <p>Depends on an unbound {@link RefreshableKeyProvider}.</p>
 */
@Module(includes = {AgeCertificateSignerModule.class, AuthMatchDataEncryptorModule.class})
public interface FakeAvsVerificationFactoryModule {

    @Binds
    FakeAvsVerificationFactory bindFakeAvsVerificationFactory(FakeAvsVerificationFactoryImpl impl);
}
