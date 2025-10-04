package org.example.age.common.client.redis;

import java.io.IOException;
import org.example.age.testing.client.TestClient;
import org.example.age.testing.json.JsonTesting;
import org.junit.jupiter.api.Test;

public final class RedisClientConfigTest {

    @Test
    public void serializeThenDeserialize() throws IOException {
        RedisClientConfig config =
                RedisClientConfig.builder().url(TestClient.localhostUrl(6379)).build();
        JsonTesting.serializeThenDeserialize(config, RedisClientConfig.class);
    }
}
