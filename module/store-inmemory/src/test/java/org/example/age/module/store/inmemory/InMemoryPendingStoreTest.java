package org.example.age.module.store.inmemory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.age.testing.CompletionStageTesting.getCompleted;

import dagger.Component;
import jakarta.inject.Singleton;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import org.example.age.module.store.inmemory.testing.FakeScheduledExecutorService;
import org.example.age.module.store.inmemory.testing.TestDependenciesModule;
import org.example.age.service.module.store.PendingStore;
import org.example.age.service.module.store.PendingStoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public final class InMemoryPendingStoreTest {

    private PendingStore<Integer> store;
    private FakeScheduledExecutorService scheduledExecutor;

    @BeforeEach
    public void createPendingStoreEtAl() {
        TestComponent component = TestComponent.create();
        PendingStoreRepository stores = component.pendingStoreRepository();
        store = stores.get("name", Integer.class);
        scheduledExecutor = component.fakeScheduledExecutorService();
    }

    @Test
    public void putThenRemove() {
        getCompleted(store.put("key", 1, expiresIn(5)));
        Optional<Integer> maybeValue1 = getCompleted(store.tryRemove("key"));
        assertThat(maybeValue1).hasValue(1);
        Optional<Integer> maybeValue2 = getCompleted(store.tryGet("key"));
        assertThat(maybeValue2).isEmpty();
    }

    @Test
    public void expire() {
        getCompleted(store.put("key", 1, expiresIn(5)));
        scheduledExecutor.runScheduledTask();
        Optional<Integer> maybeValue = getCompleted(store.tryGet("key"));
        assertThat(maybeValue).isEmpty();
    }

    @Test
    public void putExpiredValue() {
        getCompleted(store.put("key", 1, expiresIn(-5)));
        Optional<Integer> maybeValue = getCompleted(store.tryGet("key"));
        assertThat(maybeValue).isEmpty();
    }

    @Test
    public void expireOldValue() {
        getCompleted(store.put("key", 1, expiresIn(5)));
        getCompleted(store.put("key", 1, expiresIn(5)));
        scheduledExecutor.runScheduledTask();
        Optional<Integer> maybeValue = getCompleted(store.tryGet("key"));
        assertThat(maybeValue).hasValue(1);
    }

    @Test
    public void putSameValue() {
        getCompleted(store.put("key1", 1, expiresIn(5)));
        getCompleted(store.put("key2", 1, expiresIn(5)));
        Optional<Integer> maybeValue1 = getCompleted(store.tryGet("key1"));
        assertThat(maybeValue1).hasValue(1);
        Optional<Integer> maybeValue2 = getCompleted(store.tryGet("key2"));
        assertThat(maybeValue2).hasValue(1);
    }

    private static OffsetDateTime expiresIn(int minutes) {
        return OffsetDateTime.now(ZoneOffset.UTC).plus(Duration.ofMinutes(minutes));
    }

    /** Dagger component for the stores. */
    @Component(modules = {InMemoryPendingStoreModule.class, TestDependenciesModule.class})
    @Singleton
    interface TestComponent {

        static TestComponent create() {
            return DaggerInMemoryPendingStoreTest_TestComponent.create();
        }

        PendingStoreRepository pendingStoreRepository();

        FakeScheduledExecutorService fakeScheduledExecutorService();
    }
}
