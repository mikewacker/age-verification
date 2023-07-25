package org.example.age.certificate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Duration;
import org.example.age.data.SecureId;
import org.example.age.data.VerifiedUser;
import org.junit.jupiter.api.Test;

public final class SerializationUtilsTest {

    @Test
    public void serializeThenDeserialize() {
        AgeCertificate certificate = createCertificate();
        byte[] bytes = SerializationUtils.serialize(certificate);
        AgeCertificate deserializedCertificate = SerializationUtils.deserialize(bytes, 0, bytes.length);
        assertThat(deserializedCertificate).isEqualTo(certificate);
    }

    @Test
    public void error_Deserialize() {
        byte[] bytes = new byte[4];
        assertThatThrownBy(() -> SerializationUtils.deserialize(bytes, 0, bytes.length))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("deserialization failed");
    }

    private static AgeCertificate createCertificate() {
        VerificationRequest request = VerificationRequest.generateForSite("MySite", Duration.ofMinutes(5));
        VerifiedUser user = VerifiedUser.of(SecureId.generate(), 18);
        return AgeCertificate.of(request, user);
    }
}
