package org.example.age.avs.service.verification.internal;

import dagger.Binds;
import dagger.Module;
import org.example.age.common.service.crypto.internal.AgeCertificateSignerModule;
import org.example.age.common.service.crypto.internal.AuthMatchDataEncryptorModule;
import org.example.age.common.service.data.internal.DataMapperModule;
import org.example.age.test.common.service.crypto.TestSigningKeyModule;

/** Dagger module that publishes a binding for {@link FakeAvsVerificationFactory}. */
@Module(
        includes = {
            AgeCertificateSignerModule.class,
            AuthMatchDataEncryptorModule.class,
            TestSigningKeyModule.class,
            // DataMapperModule should be added in FakeAvsServiceModule, but this module is also used in tests.
            // We also add DataMapperModule here so that this module has no unbound dependencies.
            DataMapperModule.class,
        })
public interface FakeAvsVerificationFactoryModule {

    @Binds
    FakeAvsVerificationFactory bindFakeAvsVerificationFactory(FakeAvsVerificationFactoryImpl impl);
}
