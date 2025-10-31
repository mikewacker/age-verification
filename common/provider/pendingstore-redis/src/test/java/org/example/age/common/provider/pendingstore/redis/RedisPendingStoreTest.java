package org.example.age.common.provider.pendingstore.redis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.age.testing.client.WebStageTesting.await;

import dagger.BindsInstance;
import dagger.Component;
import jakarta.inject.Singleton;
import java.util.function.Supplier;
import org.example.age.common.client.redis.RedisClientConfig;
import org.example.age.common.spi.PendingStore;
import org.example.age.common.spi.PendingStoreRepository;
import org.example.age.testing.client.TestClient;
import org.example.age.testing.common.spi.PendingStoreTestTemplate;
import org.example.age.testing.env.TestEnvModule;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.JedisPooled;

public final class RedisPendingStoreTest extends PendingStoreTestTemplate {

    private static final PendingStoreRepository stores = TestComponent.create();

    @Test
    public void redisKeys() {
        await(store().put("key-redis", 1, expiration()));
        try (JedisPooled client = new JedisPooled(TestClient.dockerUri("redis", 6379))) {
            String value = client.get("age:pending:name:key-redis");
            assertThat(value).isEqualTo("1");
        }
    }

    @Override
    protected PendingStore<Integer> store() {
        return stores.get("name", Integer.class);
    }

    /** Dagger component for {@link PendingStoreRepository}. */
    @Component(modules = {RedisPendingStoreModule.class, TestEnvModule.class})
    @Singleton
    interface TestComponent extends Supplier<PendingStoreRepository> {

        static PendingStoreRepository create() {
            RedisClientConfig config = RedisClientConfig.builder()
                    .url(TestClient.dockerUrl("redis", 6379))
                    .build();
            return DaggerRedisPendingStoreTest_TestComponent.factory()
                    .create(config)
                    .get();
        }

        @Component.Factory
        interface Factory {

            TestComponent create(@BindsInstance RedisClientConfig config);
        }
    }
}
