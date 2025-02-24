package org.example.age.module.store.redis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.age.testing.CompletionStageTesting.getCompleted;

import dagger.BindsInstance;
import dagger.Component;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import org.example.age.module.store.redis.testing.TestDependenciesModule;
import org.example.age.service.module.store.PendingStore;
import org.example.age.service.module.store.PendingStoreRepository;
import org.example.age.testing.RedisExtension;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class RedisPendingStoreTest {

    @RegisterExtension
    private static final RedisExtension redis = new RedisExtension();

    private static PendingStore<Integer> store;

    @BeforeAll
    public static void createPendingStore() {
        TestComponent component = TestComponent.create(redis.port());
        PendingStoreRepository stores = component.pendingStoreRepository();
        store = stores.get("name", Integer.class);
    }

    @Test
    public void putThenGet() {
        getCompleted(store.put("key1", 1, expiresIn(300000)));
        Optional<Integer> maybeValue = getCompleted(store.tryGet("key1"));
        assertThat(maybeValue).hasValue(1);
    }

    @Test
    public void putThenRemoveThenGet() {
        getCompleted(store.put("key2", 1, expiresIn(300000)));
        Optional<Integer> maybeValue1 = getCompleted(store.tryRemove("key2"));
        assertThat(maybeValue1).hasValue(1);
        Optional<Integer> maybeValue2 = getCompleted(store.tryGet("key2"));
        assertThat(maybeValue2).isEmpty();
    }

    @Test
    public void putThenExpireThenGet() throws InterruptedException {
        getCompleted(store.put("key3", 1, expiresIn(2)));
        Thread.sleep(4);
        Optional<Integer> maybeValue = getCompleted(store.tryRemove("key3"));
        assertThat(maybeValue).isEmpty();
    }

    @Test
    public void putExpiredThenGet() {
        getCompleted(store.put("key4", 1, expiresIn(-1000)));
        Optional<Integer> maybeValue = getCompleted(store.tryRemove("key4"));
        assertThat(maybeValue).isEmpty();
    }

    @Test
    public void putThenGetFromRedis() {
        getCompleted(store.put("key5", 1, expiresIn(300000)));
        String value = redis.client().get("age:pending:name:key5");
        assertThat(value).isEqualTo("1");
    }

    private static OffsetDateTime expiresIn(int ms) {
        return OffsetDateTime.now(ZoneOffset.UTC).plus(Duration.ofMillis(ms));
    }

    /** Dagger component for the stores. */
    @Component(modules = {RedisPendingStoreModule.class, TestDependenciesModule.class})
    @Singleton
    interface TestComponent {

        static TestComponent create(int port) {
            return DaggerRedisPendingStoreTest_TestComponent.factory().create(port);
        }

        PendingStoreRepository pendingStoreRepository();

        @Component.Factory
        interface Factory {

            TestComponent create(@BindsInstance @Named("port") int port);
        }
    }
}
