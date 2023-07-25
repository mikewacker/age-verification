package org.example.age.certificate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.security.KeyPair;
import java.time.Duration;
import org.example.age.data.SecureId;
import org.example.age.data.VerifiedUser;
import org.example.age.testing.TestKeys;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public final class AgeCertificateTest {

    private static final String SITE_ID = "MySite";
    private static final Duration EXPIRES_IN = Duration.ofMinutes(5);

    private static KeyPair keyPair;
    private static KeyPair otherKeyPair;

    @BeforeAll
    public static void generateKeys() {
        keyPair = TestKeys.generateEd25519KeyPair();
        otherKeyPair = TestKeys.generateEd25519KeyPair();
    }

    @Test
    public void signThenVerifyForSite() {
        AgeCertificate certificate = createCertificate();
        byte[] signedCertificate = certificate.sign(keyPair.getPrivate());
        AgeCertificate verifiedCertificate =
                AgeCertificate.verifyForSite(signedCertificate, keyPair.getPublic(), SITE_ID);
        assertThat(verifiedCertificate).isEqualTo(certificate);
    }

    @Test
    public void error_VerifyForSite_WrongSigner() {
        AgeCertificate certificate = createCertificate();
        byte[] signedCertificate = certificate.sign(otherKeyPair.getPrivate());
        error_VerifyForSite(signedCertificate, "invalid signature");
    }

    @Test
    public void error_VerifyForSite_WrongRecipient() {
        VerificationRequest request = VerificationRequest.generateForSite("OtherSite", EXPIRES_IN);
        AgeCertificate certificate = createCertificate(request);
        byte[] signedCertificate = certificate.sign(keyPair.getPrivate());
        error_VerifyForSite(signedCertificate, "wrong recipient");
    }

    @Test
    public void error_VerifyForSite_Expired() {
        VerificationRequest request = VerificationRequest.generateForSite(SITE_ID, Duration.ofMinutes(-1));
        AgeCertificate certificate = createCertificate(request);
        byte[] signedCertificate = certificate.sign(keyPair.getPrivate());
        error_VerifyForSite(signedCertificate, "expired age certificate");
    }

    private void error_VerifyForSite(byte[] signedCertificate, String expectedMessage) {
        assertThatThrownBy(() -> AgeCertificate.verifyForSite(signedCertificate, keyPair.getPublic(), SITE_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(expectedMessage);
    }

    private static AgeCertificate createCertificate() {
        VerificationRequest request = VerificationRequest.generateForSite(SITE_ID, EXPIRES_IN);
        return createCertificate(request);
    }

    private static AgeCertificate createCertificate(VerificationRequest request) {
        VerifiedUser user = VerifiedUser.of(SecureId.generate(), 18);
        return AgeCertificate.of(request, user);
    }
}
