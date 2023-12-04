package org.example.age.avs.service.verification.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Binds;
import dagger.Module;
import org.example.age.common.service.crypto.internal.AgeCertificateSignerModule;
import org.example.age.common.service.crypto.internal.AuthMatchDataEncryptorModule;
import org.example.age.common.service.key.test.TestKeyModule;
import org.example.age.site.service.data.internal.SiteServiceJsonSerializerModule;

/**
 * Dagger module that publishes a binding for {@link FakeAvsVerificationFactory}.
 *
 * <p>Also publishes a binding for <code>@Named("service") {@link ObjectMapper}</code>.</p>
 */
@Module(
        includes = {
            AgeCertificateSignerModule.class,
            AuthMatchDataEncryptorModule.class,
            TestKeyModule.class,
            SiteServiceJsonSerializerModule.class,
        })
public interface FakeAvsVerificationFactoryModule {

    @Binds
    FakeAvsVerificationFactory bindFakeAvsVerificationFactory(FakeAvsVerificationFactoryImpl impl);
}
