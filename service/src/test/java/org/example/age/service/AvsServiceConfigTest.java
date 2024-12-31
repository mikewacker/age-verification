package org.example.age.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.Duration;
import org.example.age.service.testing.TestObjectMapper;
import org.junit.jupiter.api.Test;

public final class AvsServiceConfigTest {

    @Test
    public void serializeThenDeserialize() throws IOException {
        ObjectMapper mapper = TestObjectMapper.get();
        AvsServiceConfig config = AvsServiceConfig.builder()
                .verificationRequestExpiresIn(Duration.ofMinutes(5))
                .build();
        String json = mapper.writeValueAsString(config);
        AvsServiceConfig rtConfig = mapper.readValue(json, AvsServiceConfig.class);
        assertThat(rtConfig).isEqualTo(config);
    }
}
