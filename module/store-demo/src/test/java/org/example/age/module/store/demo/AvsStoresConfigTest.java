package org.example.age.module.store.demo;

import java.io.IOException;
import org.example.age.module.store.demo.testing.TestConfig;
import org.example.age.testing.JsonTesting;
import org.junit.jupiter.api.Test;

public final class AvsStoresConfigTest {

    @Test
    public void serializeThenDeserialize() throws IOException {
        JsonTesting.serializeThenDeserialize(TestConfig.avs(), AvsStoresConfig.class);
    }
}
