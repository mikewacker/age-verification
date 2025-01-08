package org.example.age.module.crypto.demo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.age.testing.CompletionStageTesting.assertIsCompletedWithErrorCode;

import dagger.Component;
import jakarta.inject.Singleton;
import java.util.concurrent.CompletionStage;
import org.example.age.api.AgeCertificate;
import org.example.age.api.DigitalSignature;
import org.example.age.api.SignedAgeCertificate;
import org.example.age.api.crypto.SignatureData;
import org.example.age.module.crypto.demo.testing.TestAgeCertificate;
import org.example.age.module.crypto.demo.testing.TestDependenciesModule;
import org.example.age.service.api.crypto.AgeCertificateSigner;
import org.example.age.service.api.crypto.AgeCertificateVerifier;
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
        CompletionStage<AgeCertificate> ageCertificateStage =
                ageCertificateSigner.sign(TestAgeCertificate.get()).thenCompose(ageCertificateVerifier::verify);
        assertThat(ageCertificateStage).isCompletedWithValue(TestAgeCertificate.get());
    }

    @Test
    public void error_AlgorithmNotImplemented() {
        SignedAgeCertificate signedAgeCertificate = createSignedAgeCertificate("dne", "");
        CompletionStage<AgeCertificate> ageCertificateStage = ageCertificateVerifier.verify(signedAgeCertificate);
        assertIsCompletedWithErrorCode(ageCertificateStage, 501);
    }

    @Test
    public void error_InvalidSignature() {
        SignedAgeCertificate signedAgeCertificate = createSignedAgeCertificate("secp256r1", "");
        CompletionStage<AgeCertificate> ageCertificateStage = ageCertificateVerifier.verify(signedAgeCertificate);
        assertIsCompletedWithErrorCode(ageCertificateStage, 401);
    }

    private static SignedAgeCertificate createSignedAgeCertificate(String algorithm, String data) {
        DigitalSignature signature = DigitalSignature.builder()
                .algorithm(algorithm)
                .data(SignatureData.fromString(data))
                .build();
        return SignedAgeCertificate.builder()
                .ageCertificate(TestAgeCertificate.get())
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
