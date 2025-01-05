package org.example.age.service;

import java.io.IOException;
import java.time.Duration;
import org.example.age.testing.JsonTesting;
import org.junit.jupiter.api.Test;

public final class AvsServiceConfigTest {

    @Test
    public void serializeThenDeserialize() throws IOException {
        AvsServiceConfig config = AvsServiceConfig.builder()
                .verificationRequestExpiresIn(Duration.ofMinutes(5))
                .build();
        JsonTesting.serializeThenDeserialize(config, AvsServiceConfig.class);
    }
}
