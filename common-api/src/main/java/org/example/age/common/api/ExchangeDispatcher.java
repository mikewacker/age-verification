package org.example.age.common.api;

import io.undertow.server.Connectors;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.SameThreadExecutor;
import java.util.concurrent.ExecutorService;
import org.example.age.api.Dispatcher;
import org.example.age.api.LiteHttpHandler;
import org.example.age.api.Sender;
import org.xnio.XnioExecutor;

/** {@link Dispatcher} that is backed by an {@link HttpServerExchange}. */
public final class ExchangeDispatcher implements Dispatcher {

    private final HttpServerExchange exchange;

    /** Creates the {@link Dispatcher} from the {@link HttpServerExchange}. */
    public static Dispatcher create(HttpServerExchange exchange) {
        return new ExchangeDispatcher(exchange);
    }

    @Override
    public boolean isInIoThread() {
        return exchange.isInIoThread();
    }

    @Override
    public XnioExecutor getIoThread() {
        return exchange.getIoThread();
    }

    @Override
    public ExecutorService getWorker() {
        return exchange.getConnection().getWorker();
    }

    @Override
    public <S extends Sender> void dispatch(S sender, LiteHttpHandler<S> handler) {
        exchange.dispatch(ex -> handler.handleRequest(sender, this));
    }

    @Override
    public void dispatched() {
        exchange.dispatch(SameThreadExecutor.INSTANCE, () -> {});
    }

    @Override
    public <S extends Sender> void executeHandler(S sender, LiteHttpHandler<S> handler) {
        Connectors.executeRootHandler(ex -> handler.handleRequest(sender, this), exchange);
    }

    private ExchangeDispatcher(HttpServerExchange exchange) {
        this.exchange = exchange;
    }
}
