package org.example.age.data.certificate;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import java.security.KeyPair;
import java.time.Duration;
import org.example.age.data.DataMapper;
import org.example.age.data.SecureId;
import org.example.age.data.VerifiedUser;
import org.example.age.testing.crypto.TestKeys;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public final class SignedAgeCertificateTest {

    private static KeyPair keyPair;

    @BeforeAll
    public static void generateKeys() {
        keyPair = TestKeys.generateEd25519KeyPair();
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
        DigitalSignature signature = DigitalSignature.ofBytes(new byte[1024]);
        SignedAgeCertificate signedCertificate = SignedAgeCertificate.of(certificate, signature);
        boolean wasVerified = signedCertificate.verify(keyPair.getPublic());
        assertThat(wasVerified).isFalse();
    }

    @Test
    public void serializeThenDeserialize() throws IOException {
        AgeCertificate certificate = createAgeCertificate();
        SignedAgeCertificate signedCertificate = SignedAgeCertificate.sign(certificate, keyPair.getPrivate());
        byte[] rawSignedCertificate = DataMapper.get().writeValueAsBytes(signedCertificate);
        SignedAgeCertificate rtSignedCertificate =
                DataMapper.get().readValue(rawSignedCertificate, new TypeReference<>() {});
        assertThat(rtSignedCertificate).isEqualTo(signedCertificate);
    }

    @Test
    public void signThenSerializeThenDeserializeThenVerify() throws IOException {
        AgeCertificate certificate = createAgeCertificate();
        SignedAgeCertificate signedCertificate = SignedAgeCertificate.sign(certificate, keyPair.getPrivate());
        byte[] rawSignedCertificate = DataMapper.get().writeValueAsBytes(signedCertificate);
        SignedAgeCertificate rtSignedCertificate =
                DataMapper.get().readValue(rawSignedCertificate, new TypeReference<>() {});
        boolean wasVerified = rtSignedCertificate.verify(keyPair.getPublic());
        assertThat(wasVerified).isTrue();
    }

    private static AgeCertificate createAgeCertificate() {
        VerificationRequest request = VerificationRequest.generateForSite("Site", Duration.ofMinutes(5));
        VerifiedUser user = VerifiedUser.of(SecureId.generate(), 18);
        AuthToken authToken = AuthToken.empty();
        return AgeCertificate.of(request, user, authToken);
    }
}
