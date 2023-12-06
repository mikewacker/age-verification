package org.example.age.api;

import java.util.concurrent.ExecutorService;

/**
 * Dispatches requests to the worker thread pool, and also schedules tasks.
 *
 * <p>Requests run on an IO thread, so requests that block should be dispatched to a worker thread.</p>
 *
 * <p>By default, the underlying HTTP exchange completes when the request handler returns.
 * Calling {@link #dispatch(Sender, ApiHandler)} (or {@link #dispatched()}) will prevent that from happening.</p>
 */
public interface Dispatcher {

    /** Determines if the current thread is the IO thread. */
    boolean isInIoThread();

    /** Gets the IO thread, which can also schedule tasks. */
    ScheduledExecutor getIoThread();

    /** Gets the worker thread pool. */
    ExecutorService getWorker();

    /** Dispatches this request to the worker thread pool. */
    <S extends Sender> void dispatch(S sender, ApiHandler<S> handler);

    /** Called when this request is dispatched without calling {@link #dispatch(Sender, ApiHandler)}. */
    void dispatched();

    /** Executes the handler on the dispatched thread when {@link #dispatch(Sender, ApiHandler)} was not called. */
    <S extends Sender> void executeHandler(S sender, ApiHandler<S> handler);
}
