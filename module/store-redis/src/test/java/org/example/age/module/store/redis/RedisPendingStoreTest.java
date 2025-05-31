package org.example.age.module.store.redis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.age.common.testing.WebStageTesting.await;

import dagger.Component;
import jakarta.inject.Singleton;
import org.example.age.module.store.redis.testing.RedisTestContainer;
import org.example.age.module.store.redis.testing.TestDependenciesModule;
import org.example.age.service.module.store.PendingStore;
import org.example.age.service.module.store.PendingStoreRepository;
import org.example.age.service.module.store.testing.PendingStoreTestTemplate;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import redis.clients.jedis.JedisPooled;

public final class RedisPendingStoreTest extends PendingStoreTestTemplate {

    @RegisterExtension
    private static final RedisTestContainer redis = new RedisTestContainer();

    private static PendingStoreRepository stores;

    @BeforeAll
    public static void createPendingStoreRepository() {
        TestComponent component = TestComponent.create();
        stores = component.pendingStoreRepository();
    }

    @Test
    public void redisKeys() {
        await(store().put("key-redis", 1, expiration()));

        JedisPooled client = redis.getClient();
        String value = client.get("age:pending:name:key-redis");
        assertThat(value).isEqualTo("1");
    }

    @Override
    protected PendingStore<Integer> store() {
        return stores.get("name", Integer.class);
    }

    /** Dagger component for the store. */
    @Component(modules = {RedisPendingStoreModule.class, TestDependenciesModule.class})
    @Singleton
    interface TestComponent {

        static TestComponent create() {
            return DaggerRedisPendingStoreTest_TestComponent.create();
        }

        PendingStoreRepository pendingStoreRepository();
    }
}
