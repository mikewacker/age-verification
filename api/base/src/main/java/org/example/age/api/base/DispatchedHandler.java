package org.example.age.api.base;

/**
 * Handler that runs on the worker thread when a request is manually dispatched
 * (i.e., when {@link Dispatcher#dispatched()} was called).
 *
 * <p>The worker thread should immediately call {@link Dispatcher#executeHandler(DispatchedHandler)}.</p>
 */
@FunctionalInterface
public interface DispatchedHandler {

    void handleRequest() throws Exception;
}
