package org.example.age.module.client;

import java.net.URI;
import org.example.age.testing.JsonTesting;
import org.junit.jupiter.api.Test;

public final class AvsClientsConfigTest {

    @Test
    public void serializeThenDeserialize() throws Exception {
        AvsClientsConfig config = AvsClientsConfig.builder()
                .putSiteUrls("site", new URI("http://localhost:8080").toURL())
                .build();
        JsonTesting.serializeThenDeserialize(config, AvsClientsConfig.class);
    }
}
