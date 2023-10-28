package org.example.age.common.base.store;

import static org.assertj.core.api.Assertions.assertThat;

import dagger.Component;
import java.util.concurrent.TimeUnit;
import javax.inject.Singleton;
import org.assertj.core.data.Offset;
import org.example.age.common.base.store.testing.FakeXnioExecutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public final class InMemoryPendingStoreFactoryTest {

    private PendingStore<Integer, String> store;
    private FakeXnioExecutor executor;

    @BeforeEach
    public void createPendingStoreEtAl() {
        PendingStoreFactory storeFactory = TestComponent.createPendingStoreFactory();
        store = storeFactory.create();
        executor = FakeXnioExecutor.create();
    }

    @Test
    public void putAndRemove() {
        long expiration = createExpiration();
        store.put(1, "a", expiration, executor);
        assertThat(store.tryGet(1)).hasValue("a");
        FakeXnioExecutor.ScheduledTask expirationTask = executor.getScheduledTask();

        assertThat(store.tryRemove(1)).hasValue("a");
        assertThat(store.tryGet(1)).isEmpty();
        assertThat(expirationTask.remove()).isFalse();
    }

    @Test
    public void putAndExpire() {
        long expiration = createExpiration(10);
        store.put(1, "a", expiration, executor);
        assertThat(store.tryGet(1)).hasValue("a");
        FakeXnioExecutor.ScheduledTask expirationTask = executor.getScheduledTask();
        assertThat(expirationTask.time()).isCloseTo(10L, Offset.offset(1L));
        assertThat(expirationTask.unit()).isEqualTo(TimeUnit.SECONDS);

        expirationTask.run();
        assertThat(store.tryGet(1)).isEmpty();
    }

    @Test
    public void putExpiredValue() {
        long expiration = createExpiration(-10);
        store.put(1, "a", expiration, executor);
        assertThat(store.tryGet(1)).isEmpty();
    }

    @Test
    public void updateValueAndExpireOldValue() {
        long expiration = createExpiration();
        store.put(1, "a", expiration, executor);
        assertThat(store.tryGet(1)).hasValue("a");
        FakeXnioExecutor.ScheduledTask expirationTask = executor.getScheduledTask();

        store.put(1, "a", expiration, executor);
        expirationTask.run();
        assertThat(store.tryGet(1)).hasValue("a");
    }

    @Test
    public void tryRemoveEmptyValue() {
        assertThat(store.tryRemove(1)).isEmpty();
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
