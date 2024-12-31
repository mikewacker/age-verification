package org.example.age.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.Duration;
import org.example.age.service.testing.TestObjectMapper;
import org.junit.jupiter.api.Test;

public final class SiteServiceConfigTest {

    @Test
    public void serializeThenDeserialize() throws IOException {
        ObjectMapper mapper = TestObjectMapper.get();
        SiteServiceConfig config = SiteServiceConfig.builder()
                .id("site")
                .verifiedAccountExpiresIn(Duration.ofDays(30))
                .build();
        String json = mapper.writeValueAsString(config);
        SiteServiceConfig rtConfig = mapper.readValue(json, SiteServiceConfig.class);
        assertThat(rtConfig).isEqualTo(config);
    }
}
