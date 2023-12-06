package org.example.age.avs.service.verification.internal;

import dagger.Binds;
import dagger.Module;
import java.security.PrivateKey;
import org.example.age.common.service.crypto.internal.AgeCertificateSignerModule;
import org.example.age.common.service.crypto.internal.AuthMatchDataEncryptorModule;

/**
 * Dagger module that publishes a binding for {@link FakeAvsVerificationFactory}.
 *
 * <p>Depends on an unbound <code>@Named("signing") Provider&lt;{@link PrivateKey}&gt;</code>.</p>
 */
@Module(includes = {AgeCertificateSignerModule.class, AuthMatchDataEncryptorModule.class})
public interface FakeAvsVerificationFactoryModule {

    @Binds
    FakeAvsVerificationFactory bindFakeAvsVerificationFactory(FakeAvsVerificationFactoryImpl impl);
}
