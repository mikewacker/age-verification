package org.example.age.api;

import java.util.concurrent.ExecutorService;
import org.xnio.XnioExecutor;

/**
 * Dispatches API calls to the worker thread pool, and also schedules tasks.
 *
 * <p>API calls run on an IO thread, so API calls that block should be dispatched.</p>
 *
 * <p>By default, the underlying HTTP exchange completes when the API call returns.
 * Calling {@link #dispatch(Sender, LiteHttpHandler)} (or {@link #dispatched()}) will prevent that from happening.</p>
 */
public interface Dispatcher {

    /** Determines if the current thread is the IO thread. */
    boolean isInIoThread();

    /** Gets the IO thread, which has the capability to run timed, cancellable tasks. */
    XnioExecutor getIoThread();

    /** Gets the worker thread pool. */
    ExecutorService getWorker();

    /** Dispatches this API call to the worker thread pool. */
    <S extends Sender> void dispatch(S sender, LiteHttpHandler<S> handler);

    /** Called when the API call is dispatched without calling {@link #dispatch(Sender, LiteHttpHandler)}. */
    void dispatched();

    /** Executes the handler on the dispatched thread when {@link #dispatch(Sender, LiteHttpHandler)} was not called. */
    <S extends Sender> void executeHandler(S sender, LiteHttpHandler<S> handler);
}
