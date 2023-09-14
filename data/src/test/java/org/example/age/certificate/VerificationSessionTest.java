package org.example.age.certificate;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import org.junit.jupiter.api.Test;

public final class VerificationSessionTest {

    @Test
    public void serializeThenDeserialize() {
        VerificationRequest request = VerificationRequest.generateForSite("Site", Duration.ofMinutes(5));
        VerificationSession session = VerificationSession.create(request);
        byte[] bytes = session.serialize();
        VerificationSession deserializedSession = VerificationSession.deserialize(bytes);
        assertThat(deserializedSession).isEqualTo(session);
    }
}
