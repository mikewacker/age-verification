package org.example.age.avs.client.site;

import java.io.IOException;
import org.example.age.testing.client.TestClient;
import org.example.age.testing.json.JsonTesting;
import org.junit.jupiter.api.Test;

public final class SiteClientsConfigTest {

    @Test
    public void serializeThenDeserialize() throws IOException {
        SiteClientsConfig config = SiteClientsConfig.builder()
                .putUrls("site", TestClient.localhostUrl(80))
                .build();
        JsonTesting.serializeThenDeserialize(config, SiteClientsConfig.class);
    }
}
