package org.example.age.common.service.crypto.internal;

import static org.assertj.core.api.Assertions.assertThat;

import dagger.Component;
import dagger.Module;
import java.time.Duration;
import javax.inject.Singleton;
import org.example.age.data.certificate.AgeCertificate;
import org.example.age.data.certificate.SignedAgeCertificate;
import org.example.age.data.certificate.VerificationRequest;
import org.example.age.data.crypto.AesGcmEncryptionPackage;
import org.example.age.data.crypto.BytesValue;
import org.example.age.data.crypto.DigitalSignature;
import org.example.age.data.crypto.SecureId;
import org.example.age.data.user.VerifiedUser;
import org.example.age.test.common.service.crypto.TestSigningKeyModule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public final class AgeCertificateSignerVerifierTest {

    private static AgeCertificateSigner certificateSigner;
    private static AgeCertificateVerifier certificateVerifier;

    @BeforeAll
    public static void createAgeCertificateSignerAndVerifier() {
        certificateSigner = TestSignerComponent.createAgeCertificateSigner();
        certificateVerifier = TestVerifierComponent.createAgeCertificateVerifier();
    }

    @Test
    public void signThenVerify() {
        AgeCertificate certificate = createAgeCertificate();
        SignedAgeCertificate signedCertificate = certificateSigner.sign(certificate);
        boolean wasVerified = certificateVerifier.verify(signedCertificate);
        assertThat(wasVerified).isTrue();
    }

    @Test
    public void failToVerify() {
        AgeCertificate certificate = createAgeCertificate();
        SignedAgeCertificate signedCertificate =
                SignedAgeCertificate.of(certificate, DigitalSignature.ofBytes(new byte[1024]));
        boolean wasVerified = certificateVerifier.verify(signedCertificate);
        assertThat(wasVerified).isFalse();
    }

    private static AgeCertificate createAgeCertificate() {
        VerificationRequest request = VerificationRequest.generateForSite("Site", Duration.ofMinutes(5));
        VerifiedUser user = VerifiedUser.of(SecureId.generate(), 18);
        AesGcmEncryptionPackage authToken = AesGcmEncryptionPackage.of(BytesValue.empty(), BytesValue.empty());
        return AgeCertificate.of(request, user, authToken);
    }

    /** Dagger module that binds dependencies for {@link AgeCertificateSigner}. */
    @Module(includes = {AgeCertificateSignerModule.class, TestSigningKeyModule.class})
    interface TestSignerModule {}

    /** Dagger component that provides an {@link AgeCertificateSigner}. */
    @Component(modules = TestSignerModule.class)
    @Singleton
    interface TestSignerComponent {

        static AgeCertificateSigner createAgeCertificateSigner() {
            TestSignerComponent component = DaggerAgeCertificateSignerVerifierTest_TestSignerComponent.create();
            return component.ageCertificateSigner();
        }

        AgeCertificateSigner ageCertificateSigner();
    }

    /** Dagger module that binds dependencies for {@link AgeCertificateVerifier}. */
    @Module(includes = {AgeCertificateVerifierModule.class, TestSigningKeyModule.class})
    interface TestVerifierModule {}

    /** Dagger module that provides an {@link AgeCertificateVerifier}. */
    @Component(modules = TestVerifierModule.class)
    @Singleton
    interface TestVerifierComponent {

        static AgeCertificateVerifier createAgeCertificateVerifier() {
            TestVerifierComponent component = DaggerAgeCertificateSignerVerifierTest_TestVerifierComponent.create();
            return component.ageCertificateVerifier();
        }

        AgeCertificateVerifier ageCertificateVerifier();
    }
}
