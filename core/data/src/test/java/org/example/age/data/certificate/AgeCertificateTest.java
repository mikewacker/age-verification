package org.example.age.data.certificate;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.mikewacker.drift.testing.json.JsonTester;
import java.time.Duration;
import org.example.age.data.crypto.Aes256Key;
import org.example.age.data.crypto.AesGcmEncryptionPackage;
import org.example.age.data.crypto.SecureId;
import org.example.age.data.user.VerifiedUser;
import org.junit.jupiter.api.Test;

public final class AgeCertificateTest {

    @Test
    public void serializeThenDeserialize() {
        JsonTester.serializeThenDeserialize(createAgeCertificate(), new TypeReference<>() {});
    }

    public static AgeCertificate createAgeCertificate() {
        VerificationRequest request =
                VerificationRequest.generateForSite("Site", Duration.ofMinutes(5), "http://localhost/verify");
        VerifiedUser user = VerifiedUser.of(SecureId.generate(), 18);
        AesGcmEncryptionPackage authToken =
                AesGcmEncryptionPackage.encrypt(new byte[] {1, 2, 3, 4}, Aes256Key.generate());
        return AgeCertificate.of(request, user, authToken);
    }
}
