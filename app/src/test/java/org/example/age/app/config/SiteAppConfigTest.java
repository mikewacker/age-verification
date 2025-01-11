package org.example.age.app.config;

import org.example.age.app.testing.TestConfigLoader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class SiteAppConfigTest {

    @RegisterExtension
    private static final TestConfigLoader<SiteAppConfig> configLoader = new TestConfigLoader<>(SiteAppConfig.class);

    @Test
    public void loadConfig() throws Exception {
        configLoader.load("config-site.yaml");
    }
}
