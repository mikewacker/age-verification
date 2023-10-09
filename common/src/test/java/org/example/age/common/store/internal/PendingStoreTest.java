package org.example.age.common.store.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xnio.XnioExecutor;

public final class PendingStoreTest {

    private PendingStore<Integer, String> store;
    private MockExecutor executor;

    @BeforeEach
    public void createStoreAndExecutor() {
        store = PendingStore.create();
        executor = MockExecutor.create();
    }

    @Test
    public void putAndRemove() {
        long expiration = createExpiration();
        boolean wasPut = store.put(1, "a", expiration, executor);
        assertThat(wasPut).isTrue();

        Optional<String> maybeValue = store.tryRemove(1);
        assertThat(maybeValue).hasValue("a");
        assertThat(executor.wasKeyRemoved()).isTrue();
    }

    @Test
    public void putAndExpire() {
        long expiration = createExpiration(10);
        boolean wasPut = store.put(1, "a", expiration, executor);
        assertThat(wasPut).isTrue();
        assertThat(executor.getDelay()).isCloseTo(10L, Offset.offset(1L));

        executor.executeScheduledTask();
        Optional<String> maybeValue = store.tryRemove(1);
        assertThat(maybeValue).isEmpty();
        assertThat(executor.wasKeyRemoved()).isFalse();
    }

    @Test
    public void putExpiredValue() {
        long expiration = createExpiration(-10);
        boolean wasPut = store.put(1, "a", expiration, executor);
        assertThat(wasPut).isFalse();

        Optional<String> maybeValue = store.tryRemove(1);
        assertThat(maybeValue).isEmpty();
    }

    @Test
    public void error_Put_DuplicateKey() {
        long future = (System.currentTimeMillis() / 1000) + 10;
        store.put(1, "a", future, executor);
        assertThatThrownBy(() -> store.put(1, "b", future, executor))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("duplicate key");

        Optional<String> maybeValue = store.tryRemove(1);
        assertThat(maybeValue).hasValue("a");
    }

    @Test
    public void error_Put_SchedulingFailure() {
        long expiration = createExpiration();
        executor.failOnSchedule();
        assertThatThrownBy(() -> store.put(1, "a", expiration, executor));

        Optional<String> maybeValue = store.tryRemove(1);
        assertThat(maybeValue).isEmpty();
    }

    private static long createExpiration() {
        return createExpiration(10);
    }

    private static long createExpiration(long delta) {
        long now = System.currentTimeMillis() / 1000;
        return now + delta;
    }

    /** Mock {@link XnioExecutor} that can execute a scheduled task immediately (or fail to schedule a task). */
    private static final class MockExecutor implements XnioExecutor {

        private Runnable command = null;
        private long time = 0;
        private TimeUnit unit = null;

        private boolean wasKeyRemoved = false;
        private boolean failOnSchedule = false;

        public static MockExecutor create() {
            return new MockExecutor();
        }

        @Override
        public void execute(Runnable command) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Key executeAfter(Runnable command, long time, TimeUnit unit) {
            if (failOnSchedule) {
                throw new RuntimeException();
            }

            this.command = command;
            this.time = time;
            this.unit = unit;
            Key key = mock(Key.class);
            when(key.remove()).then(invocation -> {
                wasKeyRemoved = true;
                return true;
            });
            return key;
        }

        @Override
        public Key executeAtInterval(Runnable command, long time, TimeUnit unit) {
            throw new UnsupportedOperationException();
        }

        public long getDelay() {
            assertThat(unit).isEqualTo(TimeUnit.SECONDS);
            return time;
        }

        public boolean wasKeyRemoved() {
            return wasKeyRemoved;
        }

        public void executeScheduledTask() {
            assertThat(command).isNotNull();
            command.run();
        }

        public void failOnSchedule() {
            failOnSchedule = true;
        }

        private MockExecutor() {}
    }
}
