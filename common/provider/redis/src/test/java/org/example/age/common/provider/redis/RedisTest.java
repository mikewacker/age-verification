package org.example.age.common.provider.redis;

import static org.assertj.core.api.Assertions.assertThat;

import dagger.Component;
import jakarta.inject.Singleton;
import java.util.function.Supplier;
import org.example.age.common.provider.redis.testing.TestDependenciesModule;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.JedisPooled;

public final class RedisTest {

    private static final JedisPooled client = TestComponent.create();

    @Test
    public void useClient() {
        client.set("key", "value");
        String value = client.get("key");
        assertThat(value).isEqualTo("value");
    }

    /** Dagger component for {@link JedisPooled}. */
    @Component(modules = {RedisModule.class, TestDependenciesModule.class})
    @Singleton
    interface TestComponent extends Supplier<JedisPooled> {

        static JedisPooled create() {
            return DaggerRedisTest_TestComponent.create().get();
        }
    }
}
