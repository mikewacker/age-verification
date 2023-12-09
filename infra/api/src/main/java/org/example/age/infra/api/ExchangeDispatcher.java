package org.example.age.infra.api;

import io.undertow.server.Connectors;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.SameThreadExecutor;
import java.util.concurrent.ExecutorService;
import org.example.age.api.base.ApiHandler;
import org.example.age.api.base.Dispatcher;
import org.example.age.api.base.ScheduledExecutor;
import org.example.age.api.base.Sender;

/** {@link Dispatcher} that is backed by an {@link HttpServerExchange}. */
public final class ExchangeDispatcher implements Dispatcher {

    private final HttpServerExchange exchange;
    private final ScheduledExecutor ioThread;
    private final ExecutorService worker;

    /** Creates the {@link Dispatcher} from the {@link HttpServerExchange}. */
    public static Dispatcher create(HttpServerExchange exchange) {
        return new ExchangeDispatcher(exchange);
    }

    @Override
    public boolean isInIoThread() {
        return exchange.isInIoThread();
    }

    @Override
    public ScheduledExecutor getIoThread() {
        return ioThread;
    }

    @Override
    public ExecutorService getWorker() {
        return worker;
    }

    @Override
    public <S extends Sender> void dispatch(S sender, ApiHandler<S> handler) {
        exchange.dispatch(ex -> handler.handleRequest(sender, this));
    }

    @Override
    public void dispatched() {
        exchange.dispatch(SameThreadExecutor.INSTANCE, () -> {});
    }

    @Override
    public <S extends Sender> void executeHandler(S sender, ApiHandler<S> handler) {
        Connectors.executeRootHandler(ex -> handler.handleRequest(sender, this), exchange);
    }

    private ExchangeDispatcher(HttpServerExchange exchange) {
        this.exchange = exchange;
        ioThread = XnioScheduledExecutor.create(exchange.getIoThread());
        worker = exchange.getConnection().getWorker();
    }
}
