package org.example.age.avs.provider.certificatesigner.test;

import dagger.Component;
import jakarta.inject.Singleton;
import java.security.PublicKey;
import java.util.function.Supplier;
import org.example.age.avs.spi.AgeCertificateSigner;
import org.example.age.testing.api.TestSignatures;
import org.example.age.testing.site.spi.CertificateSignerTestTemplate;

public final class TestCertificateSignerTest extends CertificateSignerTestTemplate {

    private static final AgeCertificateSigner signer = TestComponent.create();
    private static final PublicKey publicKey = TestSignatures.getKeyPair().getPublic();

    @Override
    protected AgeCertificateSigner signer() {
        return signer;
    }

    @Override
    protected PublicKey publicKey() {
        return publicKey;
    }

    /** Dagger component for {@link AgeCertificateSigner}. */
    @Component(modules = TestCertificateSignerModule.class)
    @Singleton
    interface TestComponent extends Supplier<AgeCertificateSigner> {

        static AgeCertificateSigner create() {
            return DaggerTestCertificateSignerTest_TestComponent.create().get();
        }
    }
}
