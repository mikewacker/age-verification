package org.example.age.site.provider.certificateverifier.demo;

import dagger.BindsInstance;
import dagger.Component;
import jakarta.inject.Singleton;
import java.math.BigInteger;
import java.security.PrivateKey;
import java.util.function.Supplier;
import org.example.age.common.provider.signingkey.demo.EcPublicKeyConfig;
import org.example.age.common.provider.signingkey.demo.NistP256KeyFactory;
import org.example.age.site.spi.AgeCertificateVerifier;
import org.example.age.testing.env.TestEnvModule;
import org.example.age.testing.site.spi.CertificateVerifierTestTemplate;

public final class DemoCertificateVerifierTest extends CertificateVerifierTestTemplate {

    private static final AgeCertificateVerifier verifiier = TestComponent.create();
    private static final PrivateKey privateKey = NistP256KeyFactory.createPrivate(
            new BigInteger("87808632867103956881705523559918117434194472117688001288631494927155518459976"));

    @Override
    protected AgeCertificateVerifier verifier() {
        return verifiier;
    }

    @Override
    protected PrivateKey privateKey() {
        return privateKey;
    }

    @Component(modules = {DemoCertificateVerifierModule.class, TestEnvModule.class})
    @Singleton
    interface TestComponent extends Supplier<AgeCertificateVerifier> {

        static AgeCertificateVerifier create() {
            EcPublicKeyConfig config = EcPublicKeyConfig.builder()
                    .wX(new BigInteger("61340499596180719707288738669477306360190613239883629564918816825111167687915"))
                    .wY(new BigInteger("38000387743223524528339467703153930999010297887656121516318277573781881204945"))
                    .build();
            return DaggerDemoCertificateVerifierTest_TestComponent.factory()
                    .create(config)
                    .get();
        }

        @Component.Factory
        interface Factory {

            TestComponent create(@BindsInstance EcPublicKeyConfig config);
        }
    }
}
