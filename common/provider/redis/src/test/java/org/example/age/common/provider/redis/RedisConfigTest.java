package org.example.age.common.provider.redis;

import java.io.IOException;
import org.example.age.testing.client.TestClient;
import org.example.age.testing.json.JsonTesting;
import org.junit.jupiter.api.Test;

public final class RedisConfigTest {

    @Test
    public void serializeThenDeserialize() throws IOException {
        RedisConfig config =
                RedisConfig.builder().url(TestClient.localhostUrl(6379)).build();
        JsonTesting.serializeThenDeserialize(config, RedisConfig.class);
    }
}
