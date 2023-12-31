package org.example.age.api.infra;

import io.github.mikewacker.drift.api.ApiHandler;
import io.github.mikewacker.drift.api.Dispatcher;
import io.github.mikewacker.drift.api.ScheduledExecutor;
import io.github.mikewacker.drift.api.Sender;
import io.undertow.server.Connectors;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.SameThreadExecutor;
import java.util.concurrent.ExecutorService;

/** {@link Dispatcher} that is backed by an {@link HttpServerExchange}. */
final class UndertowDispatcher implements Dispatcher {

    private final HttpServerExchange exchange;
    private final ScheduledExecutor ioThread;
    private final ExecutorService worker;

    /** Creates a {@link Dispatcher} from an {@link HttpServerExchange}. */
    public static Dispatcher create(HttpServerExchange exchange) {
        return new UndertowDispatcher(exchange);
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
    public <S extends Sender> void dispatch(S sender, ApiHandler.ZeroArg<S> handler) {
        exchange.dispatch(ex -> handler.handleRequest(sender, this));
    }

    @Override
    public <S extends Sender, A> void dispatch(S sender, A arg, ApiHandler.OneArg<S, A> handler) {
        exchange.dispatch(ex -> handler.handleRequest(sender, arg, this));
    }

    @Override
    public <S extends Sender, A1, A2> void dispatch(S sender, A1 arg1, A2 arg2, ApiHandler.TwoArg<S, A1, A2> handler) {
        exchange.dispatch(ex -> handler.handleRequest(sender, arg1, arg2, this));
    }

    @Override
    public <S extends Sender, A1, A2, A3> void dispatch(
            S sender, A1 arg1, A2 arg2, A3 arg3, ApiHandler.ThreeArg<S, A1, A2, A3> handler) {
        exchange.dispatch(ex -> handler.handleRequest(sender, arg1, arg2, arg3, this));
    }

    @Override
    public <S extends Sender, A1, A2, A3, A4> void dispatch(
            S sender, A1 arg1, A2 arg2, A3 arg3, A4 arg4, ApiHandler.FourArg<S, A1, A2, A3, A4> handler) {
        exchange.dispatch(ex -> handler.handleRequest(sender, arg1, arg2, arg3, arg4, this));
    }

    @Override
    public void dispatched() {
        exchange.dispatch(SameThreadExecutor.INSTANCE, () -> {});
    }

    @Override
    public void executeHandler(DispatchedHandler handler) {
        Connectors.executeRootHandler(ex -> handler.handleRequest(), exchange);
    }

    private UndertowDispatcher(HttpServerExchange exchange) {
        this.exchange = exchange;
        ioThread = XnioScheduledExecutor.create(exchange.getIoThread());
        worker = exchange.getConnection().getWorker();
    }
}
