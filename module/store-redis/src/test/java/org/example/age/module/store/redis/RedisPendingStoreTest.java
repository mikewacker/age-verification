package org.example.age.module.store.redis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.age.common.testing.WebStageTesting.await;

import dagger.Component;
import jakarta.inject.Singleton;
import java.util.function.Supplier;
import org.example.age.module.store.redis.testing.RedisTestContainer;
import org.example.age.module.store.redis.testing.TestDependenciesModule;
import org.example.age.service.module.store.PendingStore;
import org.example.age.service.module.store.PendingStoreRepository;
import org.example.age.service.module.store.testing.PendingStoreTestTemplate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import redis.clients.jedis.JedisPooled;

public final class RedisPendingStoreTest extends PendingStoreTestTemplate {

    private static final PendingStoreRepository stores = TestComponent.create();

    @RegisterExtension
    private static final RedisTestContainer redis = new RedisTestContainer();

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

    /** Dagger component for {@link PendingStoreRepository}. */
    @Component(modules = {RedisPendingStoreModule.class, TestDependenciesModule.class})
    @Singleton
    interface TestComponent extends Supplier<PendingStoreRepository> {

        static PendingStoreRepository create() {
            return DaggerRedisPendingStoreTest_TestComponent.create().get();
        }
    }
}
