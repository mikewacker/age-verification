package org.example.age.testing.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.example.age.api.base.Dispatcher;
import org.example.age.api.base.ScheduledExecutor;

/**
 * Stub {@link Dispatcher} that no-ops when it dispatches API calls or uses the executors.
 *
 * <p>It is always in the IO thread.</p>
 *
 * <p>It assumes that the executors only call methods with {@code execute} or {@code submit} in the name.</p>
 */
public final class StubDispatcher {

    private static final Dispatcher instance = create();

    /** Gets the stub {@link Dispatcher}. */
    public static Dispatcher get() {
        return instance;
    }

    /** Creates the stub {@link Dispatcher}. */
    private static Dispatcher create() {
        Dispatcher dispatcher = mock(Dispatcher.class);
        when(dispatcher.isInIoThread()).thenReturn(true);
        ScheduledExecutor ioThread = createStubIoThread();
        when(dispatcher.getIoThread()).thenReturn(ioThread);
        ExecutorService worker = createStubWorker();
        when(dispatcher.getWorker()).thenReturn(worker);
        return dispatcher;
    }

    /** Creates a stub {@link ScheduledExecutor} for the IO thread. */
    private static ScheduledExecutor createStubIoThread() {
        ScheduledExecutor.Key key = () -> false;
        ScheduledExecutor ioThread = mock(ScheduledExecutor.class);
        when(ioThread.executeAfter(any(), any())).thenReturn(key);
        return ioThread;
    }

    /** Creates a stub {@link ExecutorService} for the worker. */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private static ExecutorService createStubWorker() {
        Future future = StubFuture.create();
        ExecutorService worker = mock(ExecutorService.class);
        when(worker.submit(any(Callable.class))).thenReturn(future);
        when(worker.submit(any(Runnable.class))).thenReturn(future);
        when(worker.submit(any(Runnable.class), any())).thenReturn(future);
        return worker;
    }

    // static class
    private StubDispatcher() {}

    /** Stub future whose value cannot be retrieved. */
    private static final class StubFuture<V> implements Future<V> {

        public static <V> Future<V> create() {
            return new StubFuture<>();
        }

        @Override
        public V get() {
            throw new UnsupportedOperationException();
        }

        @Override
        public V get(long l, TimeUnit timeUnit) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean cancel(boolean b) {
            return false;
        }

        @Override
        public boolean isDone() {
            return false;
        }

        @Override
        public boolean isCancelled() {
            return false;
        }

        private StubFuture() {}
    }
}