package org.example.age.testing.api;

import java.time.Duration;
import org.example.age.api.base.ScheduledExecutor;

/** Stub {@link ScheduledExecutor}. */
public final class StubScheduledExecutor implements ScheduledExecutor {

    private static final ScheduledExecutor instance = new StubScheduledExecutor();

    /** Gets the stub {@link ScheduledExecutor}. */
    public static ScheduledExecutor get() {
        return instance;
    }

    @Override
    public void execute(Runnable runnable) {}

    @Override
    public Key executeAfter(Runnable command, Duration delay) {
        return () -> false;
    }

    private StubScheduledExecutor() {}
}
