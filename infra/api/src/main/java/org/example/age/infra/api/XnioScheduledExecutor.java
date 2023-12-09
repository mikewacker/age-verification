package org.example.age.infra.api;

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
        return XnioKey.create(key);
    }

    private XnioScheduledExecutor(XnioExecutor executor) {
        this.executor = executor;
    }

    /** {@link ScheduledExecutor.Key} that is backed by a {@link XnioExecutor.Key}. */
    private static final class XnioKey implements Key {

        private final XnioExecutor.Key key;

        /** Creates a {@link ScheduledExecutor.Key} from a {@link XnioExecutor.Key}. */
        public static ScheduledExecutor.Key create(XnioExecutor.Key key) {
            return new XnioKey(key);
        }

        @Override
        public boolean cancel() {
            return key.remove();
        }

        private XnioKey(XnioExecutor.Key key) {
            this.key = key;
        }
    }
}
