package org.example.age.common.store.testing;

import java.util.concurrent.TimeUnit;
import org.xnio.XnioExecutor;

/** Fake {@link XnioExecutor} that can execute a scheduled task immediately. */
public final class FakeXnioExecutor implements XnioExecutor {

    private ScheduledTask scheduledTask = null;

    public static FakeXnioExecutor create() {
        return new FakeXnioExecutor();
    }

    public ScheduledTask getScheduledTask() {
        if (scheduledTask == null) {
            throw new IllegalStateException();
        }

        return scheduledTask;
    }

    @Override
    public void execute(Runnable command) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Key executeAfter(Runnable command, long time, TimeUnit unit) {
        scheduledTask = new ScheduledTask(command, time, unit);
        return scheduledTask;
    }

    @Override
    public Key executeAtInterval(Runnable command, long time, TimeUnit unit) {
        throw new UnsupportedOperationException();
    }

    private FakeXnioExecutor() {}

    /** Scheduled task. */
    public static final class ScheduledTask implements Runnable, XnioExecutor.Key {

        private final Runnable command;
        private final long time;
        private final TimeUnit unit;

        private boolean wasRemoved = false;

        public long time() {
            return time;
        }

        public TimeUnit unit() {
            return unit;
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

        private ScheduledTask(Runnable command, long time, TimeUnit unit) {
            this.command = command;
            this.time = time;
            this.unit = unit;
        }
    }
}
