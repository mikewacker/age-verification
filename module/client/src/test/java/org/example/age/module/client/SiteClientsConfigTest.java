package org.example.age.module.client;

import org.example.age.module.client.testing.TestConfig;
import org.example.age.testing.json.JsonTesting;
import org.junit.jupiter.api.Test;

public final class SiteClientsConfigTest {

    @Test
    public void serializeThenDeserialize() throws Exception {
        JsonTesting.serializeThenDeserialize(TestConfig.siteClients(), SiteClientsConfig.class);
    }
}
