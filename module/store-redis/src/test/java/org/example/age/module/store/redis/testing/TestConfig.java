package org.example.age.module.store.redis.testing;

import org.example.age.module.store.redis.client.RedisConfig;
import org.example.age.testing.TestClient;

/** Configuration for testing. */
public final class TestConfig {

    /** Creates the configuration for Redis. */
    public static RedisConfig createRedis(int port) {
        return RedisConfig.builder().url(TestClient.createLocalhostUrl(port)).build();
    }

    private TestConfig() {} // static class
}
