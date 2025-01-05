package org.example.age.module.client;

import java.net.URI;
import org.example.age.testing.JsonTesting;
import org.junit.jupiter.api.Test;

public final class SiteClientsConfigTest {

    @Test
    public void serializeThenDeserialize() throws Exception {
        SiteClientsConfig config = SiteClientsConfig.builder()
                .avsUrl(new URI("http://localhost:8080").toURL())
                .build();
        JsonTesting.serializeThenDeserialize(config, SiteClientsConfig.class);
    }
}
