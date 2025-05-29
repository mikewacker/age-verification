package org.example.age.module.crypto.demo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.age.common.testing.WebStageTesting.await;
import static org.example.age.common.testing.WebStageTesting.awaitErrorCode;

import dagger.Component;
import jakarta.inject.Singleton;
import org.example.age.api.AgeCertificate;
import org.example.age.api.DigitalSignature;
import org.example.age.api.SignedAgeCertificate;
import org.example.age.api.crypto.SignatureData;
import org.example.age.api.testing.TestModels;
import org.example.age.module.crypto.demo.testing.TestDependenciesModule;
import org.example.age.service.module.crypto.AgeCertificateSigner;
import org.example.age.service.module.crypto.AgeCertificateVerifier;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public final class DemoAgeCertificateSignerVerifierTest {

    private static AgeCertificateSigner ageCertificateSigner;
    private static AgeCertificateVerifier ageCertificateVerifier;

    @BeforeAll
    public static void createAgeCertificateSignerAndVerifier() {
        TestAvsComponent avsComponent = TestAvsComponent.create();
        ageCertificateSigner = avsComponent.ageCertificateSigner();
        TestSiteComponent siteComponent = TestSiteComponent.create();
        ageCertificateVerifier = siteComponent.ageCertificateVerifier();
    }

    @Test
    public void signThenVerify() {
        AgeCertificate ageCertificate = TestModels.createAgeCertificate();
        AgeCertificate rtAgeCertificate =
                await(ageCertificateSigner.sign(ageCertificate).thenCompose(ageCertificateVerifier::verify));
        assertThat(rtAgeCertificate).isEqualTo(ageCertificate);
    }

    @Test
    public void error_AlgorithmNotImplemented() {
        AgeCertificate ageCertificate = TestModels.createAgeCertificate();
        SignedAgeCertificate signedAgeCertificate = signInvalid(ageCertificate, "dne");
        awaitErrorCode(ageCertificateVerifier.verify(signedAgeCertificate), 501);
    }

    @Test
    public void error_InvalidSignature() {
        AgeCertificate ageCertificate = TestModels.createAgeCertificate();
        SignedAgeCertificate signedAgeCertificate = signInvalid(ageCertificate, "secp256r1");
        awaitErrorCode(ageCertificateVerifier.verify(signedAgeCertificate), 401);
    }

    private static SignedAgeCertificate signInvalid(AgeCertificate ageCertificate, String algorithm) {
        DigitalSignature signature = DigitalSignature.builder()
                .algorithm(algorithm)
                .data(SignatureData.fromString(""))
                .build();
        return SignedAgeCertificate.builder()
                .ageCertificate(ageCertificate)
                .signature(signature)
                .build();
    }

    /** Dagger component for crypto. */
    @Component(modules = {DemoAvsCryptoModule.class, TestDependenciesModule.class})
    @Singleton
    interface TestAvsComponent {

        static TestAvsComponent create() {
            return DaggerDemoAgeCertificateSignerVerifierTest_TestAvsComponent.create();
        }

        AgeCertificateSigner ageCertificateSigner();
    }

    /** Dagger component for crypto. */
    @Component(modules = {DemoSiteCryptoModule.class, TestDependenciesModule.class})
    @Singleton
    interface TestSiteComponent {

        static TestSiteComponent create() {
            return DaggerDemoAgeCertificateSignerVerifierTest_TestSiteComponent.create();
        }

        AgeCertificateVerifier ageCertificateVerifier();
    }
}
