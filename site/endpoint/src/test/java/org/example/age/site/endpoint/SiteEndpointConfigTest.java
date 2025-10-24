package org.example.age.site.endpoint;

import org.example.age.testing.config.TestConfigParser;
import org.junit.jupiter.api.Test;

public final class SiteEndpointConfigTest {

    @Test
    public void parse() throws Exception {
        TestConfigParser.forClass(SiteEndpointConfig.class).parseLines("id: site", "verifiedAccountExpiresIn: P30D");
    }
}
