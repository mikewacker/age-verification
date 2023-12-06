package org.example.age.common.service.crypto.internal;

import static org.assertj.core.api.Assertions.assertThat;

import dagger.Component;
import java.time.Duration;
import javax.inject.Singleton;
import org.example.age.common.service.key.test.TestKeyModule;
import org.example.age.data.certificate.AgeCertificate;
import org.example.age.data.certificate.SignedAgeCertificate;
import org.example.age.data.certificate.VerificationRequest;
import org.example.age.data.crypto.AesGcmEncryptionPackage;
import org.example.age.data.crypto.BytesValue;
import org.example.age.data.crypto.DigitalSignature;
import org.example.age.data.crypto.SecureId;
import org.example.age.data.user.VerifiedUser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public final class AgeCertificateSignerVerifierTest {

    private static AgeCertificateSigner certificateSigner;
    private static AgeCertificateVerifier certificateVerifier;

    @BeforeAll
    public static void createAgeCertificateSignerAndVerifier() {
        TestComponent component = TestComponent.create();
        certificateSigner = component.ageCertificateSigner();
        certificateVerifier = component.ageCertificateVerifier();
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
        VerificationRequest request = VerificationRequest.generateForSite("Site", Duration.ofMinutes(5));
        VerifiedUser user = VerifiedUser.of(SecureId.generate(), 18);
        AesGcmEncryptionPackage authToken = AesGcmEncryptionPackage.of(BytesValue.empty(), BytesValue.empty());
        return AgeCertificate.of(request, user, authToken);
    }

    /** Dagger component that provides an {@link AgeCertificateSigner} and an {@link AgeCertificateVerifier}. */
    @Component(modules = {AgeCertificateSignerModule.class, AgeCertificateVerifierModule.class, TestKeyModule.class})
    @Singleton
    interface TestComponent {

        static TestComponent create() {
            return DaggerAgeCertificateSignerVerifierTest_TestComponent.create();
        }

        AgeCertificateSigner ageCertificateSigner();

        AgeCertificateVerifier ageCertificateVerifier();
    }
}
