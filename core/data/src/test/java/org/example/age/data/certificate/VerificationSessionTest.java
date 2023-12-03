package org.example.age.data.certificate;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import java.time.Duration;
import org.example.age.data.mapper.DataMapper;
import org.junit.jupiter.api.Test;

public final class VerificationSessionTest {

    @Test
    public void serializeThenDeserialize() throws IOException {
        VerificationRequest request = VerificationRequest.generateForSite("Site", Duration.ofMinutes(5));
        VerificationSession session = VerificationSession.create(request);
        byte[] rawSession = DataMapper.get().writeValueAsBytes(session);
        VerificationSession rtSession = DataMapper.get().readValue(rawSession, new TypeReference<>() {});
        assertThat(rtSession).isEqualTo(session);
    }
}
