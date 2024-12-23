package org.example.age.service.crypto.internal;

import static org.assertj.core.api.Assertions.assertThat;

import dagger.Component;
import jakarta.inject.Singleton;
import java.time.Duration;
import org.example.age.data.certificate.AgeCertificate;
import org.example.age.data.certificate.SignedAgeCertificate;
import org.example.age.data.certificate.VerificationRequest;
import org.example.age.data.crypto.AesGcmEncryptionPackage;
import org.example.age.data.crypto.DigitalSignature;
import org.example.age.data.crypto.SecureId;
import org.example.age.data.user.VerifiedUser;
import org.example.age.module.key.test.TestKeyModule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public final class AgeCertificateSignerVerifierTest {

    private static AgeCertificateSigner certificateSigner;
    private static AgeCertificateVerifier certificateVerifier;

    @BeforeAll
    public static void createAgeCertificateSignerAndVerifier() {
        certificateSigner = TestAvsComponent.createAgeCertificateSigner();
        certificateVerifier = TestSiteComponent.createAgeCertificateVerifier();
    }

    @Test
    public void signThenVerify() {
        AgeCertificate certificate = createAgeCertificate();
        SignedAgeCertificate signedCertificate = certificateSigner.sign(certificate);
        boolean wasVerified = certificateVerifier.verify(signedCertificate);
        assertThat(wasVerified).isTrue();
    }

    @Test
    public void verifyFailed() {
        AgeCertificate certificate = createAgeCertificate();
        DigitalSignature forgedSignature = DigitalSignature.ofBytes(new byte[32]);
        SignedAgeCertificate forgedCertificate = SignedAgeCertificate.of(certificate, forgedSignature);
        boolean wasVerified = certificateVerifier.verify(forgedCertificate);
        assertThat(wasVerified).isFalse();
    }

    private static AgeCertificate createAgeCertificate() {
        VerificationRequest request =
                VerificationRequest.generateForSite("Site", Duration.ofMinutes(5), "http://localhost/verify");
        VerifiedUser user = VerifiedUser.of(SecureId.generate(), 18);
        AesGcmEncryptionPackage authToken = AesGcmEncryptionPackage.empty();
        return AgeCertificate.of(request, user, authToken);
    }

    /** Dagger component that provides an {@link AgeCertificateSigner}. */
    @Component(modules = {SignerCryptoModule.class, TestKeyModule.class})
    @Singleton
    interface TestAvsComponent {

        static AgeCertificateSigner createAgeCertificateSigner() {
            TestAvsComponent component = DaggerAgeCertificateSignerVerifierTest_TestAvsComponent.create();
            return component.ageCertificateSigner();
        }

        AgeCertificateSigner ageCertificateSigner();
    }

    /** Dagger component that provides an {@link AgeCertificateVerifier}. */
    @Component(modules = {VerifierCryptoModule.class, TestKeyModule.class})
    @Singleton
    interface TestSiteComponent {

        static AgeCertificateVerifier createAgeCertificateVerifier() {
            TestSiteComponent component = DaggerAgeCertificateSignerVerifierTest_TestSiteComponent.create();
            return component.ageCertificateVerifier();
        }

        AgeCertificateVerifier ageCertificateVerifier();
    }
}
