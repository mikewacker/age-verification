package org.example.age.site.app.config;

import org.example.age.testing.config.TestConfigParser;
import org.junit.jupiter.api.Test;

public final class SiteAppConfigTest {

    @Test
    public void parse() throws Exception {
        TestConfigParser.forClass(SiteAppConfig.class).parseResource("config.yml");
    }
}
