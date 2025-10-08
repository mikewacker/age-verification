package org.example.age.avs.endpoint;

import java.io.IOException;
import java.time.Duration;
import org.example.age.common.api.AgeThresholds;
import org.example.age.testing.json.JsonTesting;
import org.junit.jupiter.api.Test;

public final class AvsEndpointConfigTest {

    @Test
    public void serializeThenDeserialize() throws IOException {
        AvsEndpointConfig config = AvsEndpointConfig.builder()
                .verificationRequestExpiresIn(Duration.ofMinutes(5))
                .putAgeThresholds("site", AgeThresholds.of(18))
                .build();
        JsonTesting.serializeThenDeserialize(config, AvsEndpointConfig.class);
    }
}
