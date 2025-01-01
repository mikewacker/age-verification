package org.example.age.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.time.Duration;
import org.example.age.testing.TestObjectMapper;
import org.junit.jupiter.api.Test;

public final class AvsServiceConfigTest {

    @Test
    public void serializeThenDeserialize() throws IOException {
        AvsServiceConfig config = AvsServiceConfig.builder()
                .verificationRequestExpiresIn(Duration.ofMinutes(5))
                .build();
        String json = TestObjectMapper.get().writeValueAsString(config);
        AvsServiceConfig rtConfig = TestObjectMapper.get().readValue(json, AvsServiceConfig.class);
        assertThat(rtConfig).isEqualTo(config);
    }
}
