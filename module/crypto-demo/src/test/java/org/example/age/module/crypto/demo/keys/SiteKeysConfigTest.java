package org.example.age.module.crypto.demo.keys;

import java.io.IOException;
import org.example.age.common.testing.JsonTesting;
import org.example.age.module.crypto.demo.testing.TestConfig;
import org.junit.jupiter.api.Test;

public final class SiteKeysConfigTest {

    @Test
    public void serializeThenDeserialize() throws IOException {
        JsonTesting.serializeThenDeserialize(TestConfig.siteKeys(), SiteKeysConfig.class);
    }
}
