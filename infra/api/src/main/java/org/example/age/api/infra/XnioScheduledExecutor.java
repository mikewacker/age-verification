package org.example.age.api.infra;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.example.age.api.base.ScheduledExecutor;
import org.xnio.XnioExecutor;

/** {@link ScheduledExecutor} that is backed by a {@link XnioExecutor}. */
final class XnioScheduledExecutor implements ScheduledExecutor {

    private final XnioExecutor executor;

    /** Creates a {@link ScheduledExecutor} from a {@link XnioExecutor}. */
    public static ScheduledExecutor create(XnioExecutor executor) {
        return new XnioScheduledExecutor(executor);
    }

    @Override
    public void execute(Runnable command) {
        executor.execute(command);
    }

    @Override
    public Key executeAfter(Runnable command, Duration delay) {
        XnioExecutor.Key key = executor.executeAfter(command, delay.toMillis(), TimeUnit.MILLISECONDS);
        return new XnioKey(key);
    }

    private XnioScheduledExecutor(XnioExecutor executor) {
        this.executor = executor;
    }

    /** {@link ScheduledExecutor.Key} that is backed by a {@link XnioExecutor.Key}. */
    private record XnioKey(XnioExecutor.Key key) implements Key {

        @Override
        public boolean cancel() {
            return key.remove();
        }
    }
}
