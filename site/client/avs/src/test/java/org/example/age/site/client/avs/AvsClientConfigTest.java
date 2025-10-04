package org.example.age.site.client.avs;

import java.io.IOException;
import org.example.age.testing.client.TestClient;
import org.example.age.testing.json.JsonTesting;
import org.junit.jupiter.api.Test;

public final class AvsClientConfigTest {

    @Test
    public void serializeThenDeserialize() throws IOException {
        AvsClientConfig config =
                AvsClientConfig.builder().url(TestClient.localhostUrl(80)).build();
        JsonTesting.serializeThenDeserialize(config, AvsClientConfig.class);
    }
}
