package org.example.age.module.client;

import org.example.age.common.testing.JsonTesting;
import org.example.age.module.client.testing.TestConfig;
import org.junit.jupiter.api.Test;

public final class AvsClientsConfigTest {

    @Test
    public void serializeThenDeserialize() throws Exception {
        JsonTesting.serializeThenDeserialize(TestConfig.createAvsClients(8080), AvsClientsConfig.class);
    }
}
