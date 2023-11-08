package org.example.age.common.api;

import io.undertow.server.HttpServerExchange;
import java.util.concurrent.ExecutorService;
import org.xnio.XnioExecutor;

/** Executors that can be obtained from an {@link HttpServerExchange}. */
public interface ExchangeExecutors {

    /** Gets the executors from an {@link HttpServerExchange}. */
    static ExchangeExecutors create(HttpServerExchange exchange) {
        return new ExchangeExecutorsImpl(exchange);
    }

    /** Determines if the current thread is the IO thread. */
    boolean isInIoThread();

    /** Gets the IO thread, which has the capability to run timed, cancellable tasks. */
    XnioExecutor getIoThread();

    /** Gets the worker thread pool. */
    ExecutorService getWorker();

    /** Dispatches this request to the worker thread pool. */
    <S extends Sender> void dispatch(S sender, LiteHttpHandler<S> handler);

    /** Called when the request is dispatched without calling {@link #dispatch(Sender, LiteHttpHandler)}. */
    void dispatched();

    /** Executes the handler on the dispatched thread when {@link #dispatch(Sender, LiteHttpHandler)} was not called. */
    <S extends Sender> void executeHandler(S sender, LiteHttpHandler<S> handler);
}
