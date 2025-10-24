package org.example.age.avs.client.site;

import org.example.age.testing.config.TestConfigParser;
import org.junit.jupiter.api.Test;

public final class SiteClientsConfigTest {

    @Test
    public void parse() throws Exception {
        TestConfigParser.forClass(SiteClientsConfig.class).parseLines("urls:", "  site: http://site");
    }
}
