package org.example.age.module.store.redis.client;

import static org.assertj.core.api.Assertions.assertThat;

import dagger.BindsInstance;
import dagger.Component;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import org.example.age.module.store.redis.testing.TestDependenciesModule;
import org.example.age.testing.RedisExtension;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import redis.clients.jedis.JedisPooled;

public final class RedisClientTest {

    @RegisterExtension
    private static final RedisExtension redis = new RedisExtension();

    private static JedisPooled client;

    @BeforeAll
    public static void createClient() {
        TestComponent component = TestComponent.create(redis.port());
        client = component.jedisPooled();
    }

    @Test
    public void useClient() {
        client.set("key", "value");
        String value = client.get("key");
        assertThat(value).isEqualTo("value");
    }

    /** Dagger component for the client. */
    @Component(modules = {RedisClientModule.class, TestDependenciesModule.class})
    @Singleton
    interface TestComponent {

        static TestComponent create(int port) {
            return DaggerRedisClientTest_TestComponent.factory().create(port);
        }

        JedisPooled jedisPooled();

        @Component.Factory
        interface Factory {

            TestComponent create(@BindsInstance @Named("port") int port);
        }
    }
}
