package org.example.age.common.client.redis;

import static org.assertj.core.api.Assertions.assertThat;

import dagger.BindsInstance;
import dagger.Component;
import jakarta.inject.Singleton;
import java.util.function.Supplier;
import org.example.age.testing.client.TestClient;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.JedisPooled;

public final class RedisClientTest {

    @Test
    public void useClient() {
        try (JedisPooled client = TestComponent.create()) {
            client.set("key", "value");
            String value = client.get("key");
            assertThat(value).isEqualTo("value");
        }
    }

    /** Dagger component for {@link JedisPooled}. */
    @Component(modules = RedisClientModule.class)
    @Singleton
    interface TestComponent extends Supplier<JedisPooled> {

        static JedisPooled create() {
            RedisClientConfig config = RedisClientConfig.builder()
                    .url(TestClient.dockerUrl("redis", 6379))
                    .build();
            return DaggerRedisClientTest_TestComponent.factory().create(config).get();
        }

        @Component.Factory
        interface Factory {

            TestComponent create(@BindsInstance RedisClientConfig config);
        }
    }
}
