package org.example.age.common.api;

import io.undertow.server.Connectors;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.SameThreadExecutor;
import java.util.concurrent.ExecutorService;
import org.xnio.XnioExecutor;

final class ExchangeExecutorsImpl implements ExchangeExecutors {

    private final HttpServerExchange exchange;

    public ExchangeExecutorsImpl(HttpServerExchange exchange) {
        this.exchange = exchange;
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
        exchange.dispatch(ex -> handler.handleRequest(this, sender));
    }

    @Override
    public void dispatched() {
        exchange.dispatch(SameThreadExecutor.INSTANCE, () -> {});
    }

    @Override
    public <S extends Sender> void executeHandler(S sender, LiteHttpHandler<S> handler) {
        Connectors.executeRootHandler(ex -> handler.handleRequest(this, sender), exchange);
    }
}
