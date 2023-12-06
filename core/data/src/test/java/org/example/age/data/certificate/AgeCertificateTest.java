package org.example.age.data.certificate;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Optional;
import org.example.age.api.JsonObjects;
import org.example.age.data.crypto.Aes256Key;
import org.example.age.data.crypto.AesGcmEncryptionPackage;
import org.example.age.data.crypto.SecureId;
import org.example.age.data.user.VerifiedUser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public final class AgeCertificateTest {

    private static final byte[] AUTH_DATA = "auth data".getBytes(StandardCharsets.UTF_8);

    private static Aes256Key authKey;

    @BeforeAll
    public static void generateKeys() {
        authKey = Aes256Key.generate();
    }

    @Test
    public void decryptAuthToken() {
        AgeCertificate certificate = createAgeCertificate();
        Optional<byte[]> maybeRtAuthData = certificate.authToken().tryDecrypt(authKey);
        assertThat(maybeRtAuthData).hasValue(AUTH_DATA);
    }

    @Test
    public void serializeThenDeserialize() {
        AgeCertificate certificate = createAgeCertificate();
        byte[] rawCertificate = JsonObjects.serialize(certificate);
        AgeCertificate rtCertificate = JsonObjects.deserialize(rawCertificate, new TypeReference<>() {});
        assertThat(rtCertificate).isEqualTo(certificate);
    }

    private static AgeCertificate createAgeCertificate() {
        VerificationRequest request = VerificationRequest.generateForSite("Site", Duration.ofMinutes(5));
        VerifiedUser user = VerifiedUser.of(SecureId.generate(), 18);
        AesGcmEncryptionPackage authToken = AesGcmEncryptionPackage.encrypt(AUTH_DATA, authKey);
        return AgeCertificate.of(request, user, authToken);
    }
}
