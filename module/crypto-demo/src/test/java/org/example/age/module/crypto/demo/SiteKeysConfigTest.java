package org.example.age.module.crypto.demo;

import java.io.IOException;
import org.example.age.module.crypto.demo.testing.TestConfig;
import org.example.age.testing.JsonTesting;
import org.junit.jupiter.api.Test;

public final class SiteKeysConfigTest {

    @Test
    public void serializeThenDeserialize() throws IOException {
        JsonTesting.serializeThenDeserialize(TestConfig.site(), SiteKeysConfig.class);
    }
}
