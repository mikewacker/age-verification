package org.example.age.data.certificate;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.mikewacker.drift.testing.json.JsonTester;
import java.security.KeyPair;
import org.example.age.data.crypto.DigitalSignature;
import org.example.age.data.crypto.SigningKeys;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public final class SignedAgeCertificateTest {

    private static KeyPair keyPair;

    @BeforeAll
    public static void generateKeys() {
        keyPair = SigningKeys.generateEd25519KeyPair();
    }

    @Test
    public void signThenVerify() {
        AgeCertificate certificate = AgeCertificateTest.createAgeCertificate();
        SignedAgeCertificate signedCertificate = SignedAgeCertificate.sign(certificate, keyPair.getPrivate());
        boolean wasVerified = signedCertificate.verify(keyPair.getPublic());
        assertThat(wasVerified).isTrue();
    }

    @Test
    public void verifyFailed() {
        AgeCertificate certificate = AgeCertificateTest.createAgeCertificate();
        DigitalSignature forgedSignature = DigitalSignature.ofBytes(new byte[32]);
        SignedAgeCertificate forgedCertificate = SignedAgeCertificate.of(certificate, forgedSignature);
        boolean wasVerified = forgedCertificate.verify(keyPair.getPublic());
        assertThat(wasVerified).isFalse();
    }

    @Test
    public void serializeThenDeserialize() {
        AgeCertificate certificate = AgeCertificateTest.createAgeCertificate();
        SignedAgeCertificate signedCertificate = SignedAgeCertificate.sign(certificate, keyPair.getPrivate());
        JsonTester.serializeThenDeserialize(signedCertificate, new TypeReference<>() {});
    }
}
