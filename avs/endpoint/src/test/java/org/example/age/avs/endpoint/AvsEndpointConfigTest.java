package org.example.age.avs.endpoint;

import org.example.age.testing.config.TestConfigParser;
import org.junit.jupiter.api.Test;

public final class AvsEndpointConfigTest {

    @Test
    public void parse() throws Exception {
        TestConfigParser.forClass(AvsEndpointConfig.class)
                .parseLines("verificationRequestExpiresIn: PT5M", "ageThresholds:", "  site: [13, 18]");
    }
}
