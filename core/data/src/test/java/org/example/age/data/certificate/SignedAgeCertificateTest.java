package org.example.age.data.certificate;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.time.Duration;
import org.example.age.data.crypto.Aes256Key;
import org.example.age.data.crypto.AesGcmEncryptionPackage;
import org.example.age.data.crypto.DigitalSignature;
import org.example.age.data.crypto.SecureId;
import org.example.age.data.crypto.SigningKeys;
import org.example.age.data.user.VerifiedUser;
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
        AgeCertificate certificate = createAgeCertificate();
        SignedAgeCertificate signedCertificate = SignedAgeCertificate.sign(certificate, keyPair.getPrivate());
        boolean wasVerified = signedCertificate.verify(keyPair.getPublic());
        assertThat(wasVerified).isTrue();
    }

    @Test
    public void verifyFailed() {
        AgeCertificate certificate = createAgeCertificate();
        DigitalSignature forgedSignature = DigitalSignature.ofBytes(new byte[32]);
        SignedAgeCertificate forgedCertificate = SignedAgeCertificate.of(certificate, forgedSignature);
        boolean wasVerified = forgedCertificate.verify(keyPair.getPublic());
        assertThat(wasVerified).isFalse();
    }

    @Test
    public void serializeThenDeserialize() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        AgeCertificate certificate = createAgeCertificate();
        SignedAgeCertificate signedCertificate = SignedAgeCertificate.sign(certificate, keyPair.getPrivate());
        byte[] rawSignedCertificate = mapper.writeValueAsBytes(signedCertificate);
        SignedAgeCertificate rtSignedCertificate = mapper.readValue(rawSignedCertificate, new TypeReference<>() {});
        assertThat(rtSignedCertificate).isEqualTo(signedCertificate);
    }

    @Test
    public void signThenSerializeThenDeserializeThenVerify() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        AgeCertificate certificate = createAgeCertificate();
        SignedAgeCertificate signedCertificate = SignedAgeCertificate.sign(certificate, keyPair.getPrivate());
        byte[] rawSignedCertificate = mapper.writeValueAsBytes(signedCertificate);
        SignedAgeCertificate rtSignedCertificate = mapper.readValue(rawSignedCertificate, new TypeReference<>() {});
        boolean wasVerified = rtSignedCertificate.verify(keyPair.getPublic());
        assertThat(wasVerified).isTrue();
    }

    private static AgeCertificate createAgeCertificate() {
        VerificationRequest request = VerificationRequest.generateForSite("Site", Duration.ofMinutes(5));
        VerifiedUser user = VerifiedUser.of(SecureId.generate(), 18);
        byte[] authData = "auth data".getBytes(StandardCharsets.UTF_8);
        Aes256Key authKey = Aes256Key.generate();
        AesGcmEncryptionPackage authToken = AesGcmEncryptionPackage.encrypt(authData, authKey);
        return AgeCertificate.of(request, user, authToken);
    }
}
