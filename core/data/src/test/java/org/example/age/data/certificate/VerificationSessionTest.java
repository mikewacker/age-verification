package org.example.age.data.certificate;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import java.time.Duration;
import org.example.age.data.json.JsonValues;
import org.junit.jupiter.api.Test;

public final class VerificationSessionTest {

    @Test
    public void serializeThenDeserialize() {
        VerificationRequest request =
                VerificationRequest.generateForSite("Site", Duration.ofMinutes(5), "http://localhost/verify");
        VerificationSession session = VerificationSession.generate(request);
        byte[] rawSession = JsonValues.serialize(session);
        VerificationSession rtSession = JsonValues.deserialize(rawSession, new TypeReference<>() {});
        assertThat(rtSession).isEqualTo(session);
    }
}
