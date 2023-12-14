package org.example.age.api.base;

import java.util.concurrent.ExecutorService;

/**
 * Dispatches a request to the worker thread pool, and also schedules tasks.
 *
 * <p>Requests run on an IO thread, so requests that block should be dispatched to a worker thread.</p>
 *
 * <p>The behavior is undefined if a response is not sent and the request is not dispatched.
 * If a response is not sent, the request should be dispatched via {@link #dispatch} (or {@link #dispatched()}).</p>
 */
public interface Dispatcher {

    /** Determines if the current thread is the IO thread. */
    boolean isInIoThread();

    /** Gets the IO thread, which can also schedule tasks. */
    ScheduledExecutor getIoThread();

    /** Gets the worker thread pool. */
    ExecutorService getWorker();

    /** Dispatches this request to the worker thread pool. */
    <S extends Sender> void dispatch(S sender, ApiHandler.ZeroArg<S> handler);

    /** Dispatches this request to the worker thread pool. */
    <S extends Sender, A> void dispatch(S sender, A arg, ApiHandler.OneArg<S, A> handler);

    /** Dispatches this request to the worker thread pool. */
    <S extends Sender, A1, A2> void dispatch(S sender, A1 arg, A2 arg2, ApiHandler.TwoArg<S, A1, A2> handler);

    /** Dispatches this request to the worker thread pool. */
    <S extends Sender, A1, A2, A3> void dispatch(
            S sender, A1 arg, A2 arg2, A3 arg3, ApiHandler.ThreeArg<S, A1, A2, A3> handler);

    /** Dispatches this request to the worker thread pool. */
    <S extends Sender, A1, A2, A3, A4> void dispatch(
            S sender, A1 arg, A2 arg2, A3 arg3, A4 arg4, ApiHandler.FourArg<S, A1, A2, A3, A4> handler);

    /**
     * Called when this request is manually dispatched without calling {@link #dispatch}.
     *
     * <p>The worker thread should immediately call {@link #executeHandler(DispatchedHandler)}.</p>
     */
    void dispatched();

    /** Called on the worker thread when a request is manually dispatched (i.e., {@link #dispatched()} is called). */
    <S extends Sender> void executeHandler(DispatchedHandler handler);

    /** Handler for the worker thread when a request is manually dispatched (i.e., {@link #dispatched()} is called). */
    @FunctionalInterface
    interface DispatchedHandler {

        void handleRequest() throws Exception;
    }
}
