package org.example.age.common.service.store.inmemory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.core.type.TypeReference;
import dagger.Component;
import java.util.concurrent.TimeUnit;
import javax.inject.Singleton;
import org.assertj.core.data.Offset;
import org.example.age.common.service.store.PendingStore;
import org.example.age.common.service.store.PendingStoreFactory;
import org.example.age.testing.api.FakeXnioExecutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public final class InMemoryPendingStoreFactoryTest {

    private PendingStoreFactory storeFactory;

    private FakeXnioExecutor executor;

    @BeforeEach
    public void createPendingStoreFactoryEtAl() {
        storeFactory = TestComponent.createPendingStoreFactory();
        executor = FakeXnioExecutor.create();
    }

    @Test
    public void putAndRemove() {
        PendingStore<String> store = storeFactory.getOrCreate("name", new TypeReference<>() {});
        long expiration = createExpiration();
        store.put("key", "value", expiration, executor);
        assertThat(store.tryGet("key")).hasValue("value");
        FakeXnioExecutor.ScheduledTask expirationTask = executor.getLastScheduledTask();

        assertThat(store.tryRemove("key")).hasValue("value");
        assertThat(store.tryGet("key")).isEmpty();
        assertThat(expirationTask.remove()).isFalse();
    }

    @Test
    public void putAndExpire() {
        PendingStore<String> store = storeFactory.getOrCreate("name", new TypeReference<>() {});
        long expiration = createExpiration(10);
        store.put("key", "value", expiration, executor);
        assertThat(store.tryGet("key")).hasValue("value");
        FakeXnioExecutor.ScheduledTask expirationTask = executor.getLastScheduledTask();
        assertThat(expirationTask.getTime()).isCloseTo(10L, Offset.offset(1L));
        assertThat(expirationTask.getUnit()).isEqualTo(TimeUnit.SECONDS);

        expirationTask.run();
        assertThat(store.tryGet("key")).isEmpty();
    }

    @Test
    public void putExpiredValue() {
        PendingStore<String> store = storeFactory.getOrCreate("name", new TypeReference<>() {});
        long expiration = createExpiration(-10);
        store.put("key", "value", expiration, executor);
        assertThat(store.tryGet("key")).isEmpty();
    }

    @Test
    public void updateValueAndExpireOldValue() {
        PendingStore<String> store = storeFactory.getOrCreate("name", new TypeReference<>() {});
        long expiration = createExpiration();
        store.put("key", "value", expiration, executor);
        assertThat(store.tryGet("key")).hasValue("value");
        FakeXnioExecutor.ScheduledTask oldExpirationTask = executor.getLastScheduledTask();

        store.put("key", "value", expiration, executor);
        oldExpirationTask.run();
        assertThat(store.tryGet("key")).hasValue("value");
    }

    @Test
    public void tryRemoveEmptyValue() {
        PendingStore<String> store = storeFactory.getOrCreate("name", new TypeReference<>() {});
        assertThat(store.tryRemove("key")).isEmpty();
    }

    @Test
    public void putTwoKeysWithSameValue() {
        PendingStore<String> store = storeFactory.getOrCreate("name", new TypeReference<>() {});
        long expiration = createExpiration();
        store.put("key1", "value", expiration, executor);
        store.put("key2", "value", expiration, executor);
        assertThat(store.tryGet("key1")).hasValue("value");
        assertThat(store.tryGet("key2")).hasValue("value");
    }

    @Test
    public void error_InconsistentValueType() {
        storeFactory.<String>getOrCreate("name", new TypeReference<>() {});
        assertThatThrownBy(() -> storeFactory.<Integer>getOrCreate("name", new TypeReference<>() {}))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(
                        "inconsistent value type for name\n  create: class java.lang.String\n  get: class java.lang.Integer");
    }

    private static long createExpiration() {
        return createExpiration(10);
    }

    private static long createExpiration(long expiresIn) {
        long now = System.currentTimeMillis() / 1000;
        return now + expiresIn;
    }

    /** Dagger component that provides a {@link PendingStoreFactory}. */
    @Component(modules = InMemoryPendingStoreFactoryModule.class)
    @Singleton
    interface TestComponent {

        static PendingStoreFactory createPendingStoreFactory() {
            TestComponent component = DaggerInMemoryPendingStoreFactoryTest_TestComponent.create();
            return component.pendingStoreFactory();
        }

        PendingStoreFactory pendingStoreFactory();
    }
}
