package org.example.age.module.client;

import org.example.age.module.client.testing.TestConfig;
import org.example.age.testing.JsonTesting;
import org.junit.jupiter.api.Test;

public final class AvsClientsConfigTest {

    @Test
    public void serializeThenDeserialize() throws Exception {
        JsonTesting.serializeThenDeserialize(TestConfig.createAvs(8080), AvsClientsConfig.class);
    }
}
