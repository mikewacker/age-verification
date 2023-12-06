package org.example.age.data.certificate;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import java.time.Duration;
import org.example.age.api.JsonObjects;
import org.junit.jupiter.api.Test;

public final class VerificationSessionTest {

    @Test
    public void serializeThenDeserialize() {
        VerificationRequest request = VerificationRequest.generateForSite("Site", Duration.ofMinutes(5));
        VerificationSession session = VerificationSession.create(request);
        byte[] rawSession = JsonObjects.serialize(session);
        VerificationSession rtSession = JsonObjects.deserialize(rawSession, new TypeReference<>() {});
        assertThat(rtSession).isEqualTo(session);
    }
}
