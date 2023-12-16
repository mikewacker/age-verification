package org.example.age.service.verification.internal.avs;

import dagger.Binds;
import dagger.Module;
import org.example.age.service.crypto.internal.common.AgeCertificateSignerModule;
import org.example.age.service.crypto.internal.common.AuthMatchDataEncryptorModule;
import org.example.age.service.module.key.common.RefreshableKeyProvider;

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
