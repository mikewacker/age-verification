package org.example.age.avs.service.config;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.Duration;
import org.junit.jupiter.api.Test;

public final class AvsConfigTest {

    @Test
    public void serializeThenDeserialize() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        AvsConfig avsConfig = AvsConfig.builder()
                .verificationSessionExpiresIn(Duration.ofMinutes(5).toSeconds())
                .build();
        byte[] rawAvsConfig = mapper.writeValueAsBytes(avsConfig);
        AvsConfig rtAvsConfig = mapper.readValue(rawAvsConfig, new TypeReference<>() {});
        assertThat(rtAvsConfig).isEqualTo(avsConfig);
    }
}
