package org.example.age.app.config;

import org.example.age.app.testing.TestConfigLoader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class AvsAppConfigTest {

    @RegisterExtension
    private static final TestConfigLoader<AvsAppConfig> configLoader = new TestConfigLoader<>(AvsAppConfig.class);

    @Test
    public void loadConfig() throws Exception {
        configLoader.load("config-avs.yaml");
    }
}
