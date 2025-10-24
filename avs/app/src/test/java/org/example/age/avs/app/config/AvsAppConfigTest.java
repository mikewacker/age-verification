package org.example.age.avs.app.config;

import org.example.age.testing.config.TestConfigParser;
import org.junit.jupiter.api.Test;

public final class AvsAppConfigTest {

    @Test
    public void parse() throws Exception {
        TestConfigParser.forClass(AvsAppConfig.class).parseResource("config.yml");
    }
}
