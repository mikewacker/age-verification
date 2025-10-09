package org.example.age.site.provider.certificateverifier.test;

import dagger.Component;
import jakarta.inject.Singleton;
import java.security.PrivateKey;
import java.util.function.Supplier;
import org.example.age.site.spi.AgeCertificateVerifier;
import org.example.age.testing.api.TestSignatures;
import org.example.age.testing.site.spi.CertificateVerifierTestTemplate;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public final class TestCertificateVerifierTest extends CertificateVerifierTestTemplate {

    private static final AgeCertificateVerifier verifier = TestComponent.create();
    private static final PrivateKey privateKey = TestSignatures.getKeyPair().getPrivate();

    @Disabled
    @Test
    @Override
    public void error_AlgorithmNotImplemented() {}

    @Override
    protected AgeCertificateVerifier verifier() {
        return verifier;
    }

    @Override
    protected PrivateKey privateKey() {
        return privateKey;
    }

    /** Dagger component for {@link AgeCertificateVerifier}. */
    @Component(modules = TestCertificateVerifierModule.class)
    @Singleton
    interface TestComponent extends Supplier<AgeCertificateVerifier> {

        static AgeCertificateVerifier create() {
            return DaggerTestCertificateVerifierTest_TestComponent.create().get();
        }
    }
}
