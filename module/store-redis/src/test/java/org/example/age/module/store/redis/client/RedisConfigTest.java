package org.example.age.module.store.redis.client;

import java.io.IOException;
import org.example.age.common.testing.JsonTesting;
import org.example.age.module.store.redis.testing.TestConfig;
import org.junit.jupiter.api.Test;

public final class RedisConfigTest {

    @Test
    public void serializeThenDeserialize() throws IOException {
        JsonTesting.serializeThenDeserialize(TestConfig.redis(), RedisConfig.class);
    }
}
