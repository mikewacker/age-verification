package org.example.age.site.endpoint;

import java.io.IOException;
import java.time.Duration;
import org.example.age.testing.json.JsonTesting;
import org.junit.jupiter.api.Test;

public final class SiteEndpointConfigTest {

    @Test
    public void serializeThenDeserialize() throws IOException {
        SiteEndpointConfig config = SiteEndpointConfig.builder()
                .id("site")
                .verifiedAccountExpiresIn(Duration.ofDays(30))
                .build();
        JsonTesting.serializeThenDeserialize(config, SiteEndpointConfig.class);
    }
}
