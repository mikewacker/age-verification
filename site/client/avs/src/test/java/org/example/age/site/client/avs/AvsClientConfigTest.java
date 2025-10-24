package org.example.age.site.client.avs;

import org.example.age.testing.config.TestConfigParser;
import org.junit.jupiter.api.Test;

public final class AvsClientConfigTest {

    @Test
    public void parse() throws Exception {
        TestConfigParser.forClass(AvsClientConfig.class).parseLines("url: http://avs");
    }
}
