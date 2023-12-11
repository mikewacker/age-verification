package org.example.age.avs.service.verification.internal;

import dagger.Binds;
import dagger.Module;
import org.example.age.module.key.common.RefreshableKeyProvider;
import org.example.age.service.common.crypto.internal.AgeCertificateSignerModule;
import org.example.age.service.common.crypto.internal.AuthMatchDataEncryptorModule;

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
