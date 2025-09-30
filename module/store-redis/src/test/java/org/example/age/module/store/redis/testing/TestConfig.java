package org.example.age.module.store.redis.testing;

import org.example.age.module.store.redis.client.RedisConfig;
import org.example.age.testing.util.TestClient;

/** Configuration for testing. */
public final class TestConfig {

    private static final RedisConfig redis = RedisConfig.builder()
            .url(TestClient.localhostUrl(RedisTestContainer.PORT))
            .build();

    /** Gets the configuration for Redis. */
    public static RedisConfig redis() {
        return redis;
    }

    private TestConfig() {} // static class
}
