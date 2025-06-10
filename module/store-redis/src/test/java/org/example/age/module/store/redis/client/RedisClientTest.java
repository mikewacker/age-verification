package org.example.age.module.store.redis.client;

import static org.assertj.core.api.Assertions.assertThat;

import dagger.Component;
import jakarta.inject.Singleton;
import java.util.function.Supplier;
import org.example.age.module.store.redis.testing.RedisTestContainer;
import org.example.age.module.store.redis.testing.TestDependenciesModule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import redis.clients.jedis.JedisPooled;

public final class RedisClientTest {

    private static final JedisPooled client = TestComponent.create();

    @RegisterExtension
    private static final RedisTestContainer redis = new RedisTestContainer();

    @Test
    public void useClient() {
        client.set("key", "value");
        String value = client.get("key");
        assertThat(value).isEqualTo("value");
    }

    /** Dagger component for {@link JedisPooled}. */
    @Component(modules = {RedisClientModule.class, TestDependenciesModule.class})
    @Singleton
    interface TestComponent extends Supplier<JedisPooled> {

        static JedisPooled create() {
            return DaggerRedisClientTest_TestComponent.create().get();
        }
    }
}
