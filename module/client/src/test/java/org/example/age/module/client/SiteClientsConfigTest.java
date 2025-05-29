package org.example.age.module.client;

import org.example.age.common.testing.JsonTesting;
import org.example.age.module.client.testing.TestConfig;
import org.junit.jupiter.api.Test;

public final class SiteClientsConfigTest {

    @Test
    public void serializeThenDeserialize() throws Exception {
        JsonTesting.serializeThenDeserialize(TestConfig.createSiteClients(8080), SiteClientsConfig.class);
    }
}
