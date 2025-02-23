package org.example.age.module.store.redis.testing;

import java.net.URI;
import java.net.URL;
import org.example.age.module.store.redis.RedisConfig;

/** Configuration for testing. */
public final class TestConfig {

    /** Creates the {@link RedisConfig}. */
    public static RedisConfig createRedis(int port) {
        URL url = createLocalhostUrl(port);
        return RedisConfig.builder().url(url).build();
    }

    /** Create a URL for localhost. */
    private static URL createLocalhostUrl(int port) {
        try {
            return new URI(String.format("http://localhost:%d", port)).toURL();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // static class
    private TestConfig() {}
}
