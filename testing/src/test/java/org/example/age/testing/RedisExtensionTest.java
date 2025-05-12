package org.example.age.testing;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import redis.clients.jedis.params.SetParams;

public final class RedisExtensionTest {

    @RegisterExtension
    private static final RedisExtension redis = new RedisExtension();

    @Test
    public void useRedis() {
        redis.client().set("key", "value");
        String value = redis.client().get("key");
        assertThat(value).isEqualTo("value");
    }

    @Test
    public void setWithNxAndGetOptions() { // requires Redis 7.0.0
        redis.client().setGet("test", "1", new SetParams().nx());
    }
}
