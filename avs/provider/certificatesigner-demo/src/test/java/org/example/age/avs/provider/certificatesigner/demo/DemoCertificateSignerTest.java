package org.example.age.avs.provider.certificatesigner.demo;

import dagger.BindsInstance;
import dagger.Component;
import jakarta.inject.Singleton;
import java.math.BigInteger;
import java.security.PublicKey;
import java.security.spec.ECPoint;
import java.util.function.Supplier;
import org.example.age.avs.spi.AgeCertificateSigner;
import org.example.age.common.provider.signingkey.demo.EcPrivateKeyConfig;
import org.example.age.common.provider.signingkey.demo.NistP256KeyFactory;
import org.example.age.testing.env.TestEnvModule;
import org.example.age.testing.site.spi.CertificateSignerTestTemplate;

public final class DemoCertificateSignerTest extends CertificateSignerTestTemplate {

    private static final AgeCertificateSigner signer = TestComponent.create();
    private static final PublicKey publicKey = NistP256KeyFactory.createPublic(new ECPoint(
            new BigInteger("61340499596180719707288738669477306360190613239883629564918816825111167687915"),
            new BigInteger("38000387743223524528339467703153930999010297887656121516318277573781881204945")));

    @Override
    protected AgeCertificateSigner signer() {
        return signer;
    }

    @Override
    protected PublicKey publicKey() {
        return publicKey;
    }

    @Component(modules = {DemoCertificateSignerModule.class, TestEnvModule.class})
    @Singleton
    interface TestComponent extends Supplier<AgeCertificateSigner> {

        static AgeCertificateSigner create() {
            EcPrivateKeyConfig config = EcPrivateKeyConfig.builder()
                    .s(new BigInteger("87808632867103956881705523559918117434194472117688001288631494927155518459976"))
                    .build();
            return DaggerDemoCertificateSignerTest_TestComponent.factory()
                    .create(config)
                    .get();
        }

        @Component.Factory
        interface Factory {

            TestComponent create(@BindsInstance EcPrivateKeyConfig config);
        }
    }
}
