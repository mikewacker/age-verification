package org.example.age.common.service.store.testing;

import java.util.concurrent.TimeUnit;
import org.xnio.XnioExecutor;

/** Fake {@link XnioExecutor} where scheduled tasks have to be run manually. */
public final class FakeXnioExecutor implements XnioExecutor {

    private ScheduledTask lastScheduledTask = null;

    public static FakeXnioExecutor create() {
        return new FakeXnioExecutor();
    }

    public ScheduledTask getLastScheduledTask() {
        if (lastScheduledTask == null) {
            throw new IllegalStateException();
        }

        return lastScheduledTask;
    }

    @Override
    public void execute(Runnable command) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Key executeAfter(Runnable command, long time, TimeUnit unit) {
        lastScheduledTask = new ScheduledTask(command, time, unit);
        return lastScheduledTask;
    }

    @Override
    public Key executeAtInterval(Runnable command, long time, TimeUnit unit) {
        throw new UnsupportedOperationException();
    }

    private FakeXnioExecutor() {}

    /** Scheduled task that can be run manually. */
    public static final class ScheduledTask implements Runnable, Key {

        private final Runnable command;
        private final long time;
        private final TimeUnit unit;

        private boolean wasRemoved = false;

        public long getTime() {
            return time;
        }

        public TimeUnit getUnit() {
            return unit;
        }

        @Override
        public void run() {
            if (wasRemoved) {
                return;
            }

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
