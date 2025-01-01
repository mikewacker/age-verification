package org.example.age.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.time.Duration;
import org.example.age.testing.TestObjectMapper;
import org.junit.jupiter.api.Test;

public final class SiteServiceConfigTest {

    @Test
    public void serializeThenDeserialize() throws IOException {
        SiteServiceConfig config = SiteServiceConfig.builder()
                .id("site")
                .verifiedAccountExpiresIn(Duration.ofDays(30))
                .build();
        String json = TestObjectMapper.get().writeValueAsString(config);
        SiteServiceConfig rtConfig = TestObjectMapper.get().readValue(json, SiteServiceConfig.class);
        assertThat(rtConfig).isEqualTo(config);
    }
}
