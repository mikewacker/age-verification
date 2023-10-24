package org.example.age.common.store.internal;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.TimeUnit;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xnio.XnioExecutor;

public final class PendingStoreTest {

    private PendingStore<Integer, String> store;
    private MockExecutor executor;

    @BeforeEach
    public void createStoreEtAl() {
        store = PendingStore.create();
        executor = MockExecutor.create();
    }

    @Test
    public void putAndRemove() {
        long expiration = createExpiration();
        store.put(1, "a", expiration, executor);
        assertThat(store.tryGet(1)).hasValue("a");
        ScheduledTask expirationTask = executor.getExpirationTask();

        assertThat(store.tryRemove(1)).hasValue("a");
        assertThat(store.tryGet(1)).isEmpty();
        assertThat(expirationTask.remove()).isFalse();
    }

    @Test
    public void putAndExpire() {
        long expiration = createExpiration(10);
        store.put(1, "a", expiration, executor);
        assertThat(store.tryGet(1)).hasValue("a");
        ScheduledTask expirationTask = executor.getExpirationTask();
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
        ScheduledTask expirationTask = executor.getExpirationTask();

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

    private static long createExpiration(long delta) {
        long now = System.currentTimeMillis() / 1000;
        return now + delta;
    }

    /** Mock {@link XnioExecutor} that can execute a scheduled task immediately. */
    private static final class MockExecutor implements XnioExecutor {

        private ScheduledTask expirationTask = null;

        public static MockExecutor create() {
            return new MockExecutor();
        }

        @Override
        public void execute(Runnable command) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Key executeAfter(Runnable command, long time, TimeUnit unit) {
            expirationTask = ScheduledTask.create(command, time, unit);
            return expirationTask;
        }

        @Override
        public Key executeAtInterval(Runnable command, long time, TimeUnit unit) {
            throw new UnsupportedOperationException();
        }

        public ScheduledTask getExpirationTask() {
            return expirationTask;
        }

        private MockExecutor() {}
    }

    /** Scheduled task that can be executed immediately. */
    private static final class ScheduledTask implements Runnable, XnioExecutor.Key {

        private final Runnable command;
        private final long time;
        private final TimeUnit unit;

        private boolean wasRemoved = false;

        public static ScheduledTask create(Runnable command, long time, TimeUnit unit) {
            return new ScheduledTask(command, time, unit);
        }

        @Override
        public void run() {
            wasRemoved = true;
            command.run();
        }

        @Override
        public boolean remove() {
            boolean result = !wasRemoved;
            wasRemoved = true;
            return result;
        }

        public long time() {
            return time;
        }

        public TimeUnit unit() {
            return unit;
        }

        private ScheduledTask(Runnable command, long time, TimeUnit unit) {
            this.command = command;
            this.time = time;
            this.unit = unit;
        }
    }
}
