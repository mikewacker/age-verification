package org.example.age.module.crypto.test;

import dagger.Component;
import jakarta.inject.Singleton;
import java.util.function.Supplier;
import org.example.age.avs.spi.AgeCertificateSigner;
import org.example.age.service.module.crypto.testing.AgeCertificateSignerVerifierTestTemplate;
import org.example.age.site.spi.AgeCertificateVerifier;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public final class TestAgeCertificateSignerVerifierTest extends AgeCertificateSignerVerifierTestTemplate {

    private static final AgeCertificateSigner signer = TestAvsComponent.create();
    private static final AgeCertificateVerifier verifier = TestSiteComponent.create();

    @Disabled
    @Test
    @Override
    public void error_AlgorithmNotImplemented() {}

    @Override
    protected AgeCertificateSigner signer() {
        return signer;
    }

    @Override
    protected AgeCertificateVerifier verifier() {
        return verifier;
    }

    /** Dagger component for {@link AgeCertificateSigner}. */
    @Component(modules = TestAvsCryptoModule.class)
    @Singleton
    interface TestAvsComponent extends Supplier<AgeCertificateSigner> {

        static AgeCertificateSigner create() {
            return DaggerTestAgeCertificateSignerVerifierTest_TestAvsComponent.create()
                    .get();
        }
    }

    /** Dagger component for {@link AgeCertificateVerifier}. */
    @Component(modules = TestSiteCryptoModule.class)
    @Singleton
    interface TestSiteComponent extends Supplier<AgeCertificateVerifier> {

        static AgeCertificateVerifier create() {
            return DaggerTestAgeCertificateSignerVerifierTest_TestSiteComponent.create()
                    .get();
        }
    }
}
