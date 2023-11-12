package org.example.age.testing.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.example.age.api.Dispatcher;
import org.example.age.api.LiteHttpHandler;
import org.example.age.api.Sender;
import org.xnio.XnioExecutor;

/**
 * Stub {@link Dispatcher} that no-ops when it dispatches API calls or uses the executors.
 *
 * <p>It is always in the IO thread.</p>
 *
 * <p>It assumes that the executors only call methods with {@code execute} or {@code submit} in the name.</p>
 */
public final class StubDispatcher implements Dispatcher {

    private static final Dispatcher instance = create();

    private final XnioExecutor ioThread;
    private final ExecutorService worker;

    /** Gets the stub {@link Dispatcher}. */
    public static Dispatcher get() {
        return instance;
    }

    @Override
    public boolean isInIoThread() {
        return true;
    }

    @Override
    public XnioExecutor getIoThread() {
        return ioThread;
    }

    @Override
    public ExecutorService getWorker() {
        return worker;
    }

    @Override
    public <S extends Sender> void dispatch(S sender, LiteHttpHandler<S> handler) {}

    @Override
    public void dispatched() {}

    @Override
    public <S extends Sender> void executeHandler(S sender, LiteHttpHandler<S> handler) {}

    /** Creates a stub {@link Dispatcher}. */
    private static Dispatcher create() {
        XnioExecutor ioThread = createStubIoThread();
        ExecutorService worker = createStubWorker();
        return new StubDispatcher(ioThread, worker);
    }

    /** Creates a stub {@link XnioExecutor} for the IO thread. */
    private static XnioExecutor createStubIoThread() {
        XnioExecutor.Key key = () -> false;
        XnioExecutor ioThread = mock(XnioExecutor.class);
        when(ioThread.executeAfter(any(), anyLong(), any())).thenReturn(key);
        when(ioThread.executeAtInterval(any(), anyLong(), any())).thenReturn(key);
        return ioThread;
    }

    /** Creates a stub {@link ExecutorService} for the worker. */
    private static ExecutorService createStubWorker() {
        Future future = StubFuture.create();
        ExecutorService worker = mock(ExecutorService.class);
        when(worker.submit(any(Callable.class))).thenReturn(future);
        when(worker.submit(any(Runnable.class))).thenReturn(future);
        when(worker.submit(any(Runnable.class), any())).thenReturn(future);
        return worker;
    }

    private StubDispatcher(XnioExecutor ioThread, ExecutorService worker) {
        this.ioThread = ioThread;
        this.worker = worker;
    }

    /** Stub future whose value cannot be retrieved. */
    private static final class StubFuture<V> implements Future<V> {

        public static <T> Future<T> create() {
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
