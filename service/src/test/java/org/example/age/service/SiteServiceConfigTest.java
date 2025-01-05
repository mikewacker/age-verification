package org.example.age.service;

import java.io.IOException;
import java.time.Duration;
import org.example.age.testing.JsonTesting;
import org.junit.jupiter.api.Test;

public final class SiteServiceConfigTest {

    @Test
    public void serializeThenDeserialize() throws IOException {
        SiteServiceConfig config = SiteServiceConfig.builder()
                .id("site")
                .verifiedAccountExpiresIn(Duration.ofDays(30))
                .build();
        JsonTesting.serializeThenDeserialize(config, SiteServiceConfig.class);
    }
}
