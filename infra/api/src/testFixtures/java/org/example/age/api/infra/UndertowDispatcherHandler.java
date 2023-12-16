package org.example.age.api.infra;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import java.util.concurrent.Executor;
import org.example.age.api.base.ApiHandler;
import org.example.age.api.base.Dispatcher;
import org.example.age.api.base.Sender;

/** Test {@link HttpHandler} that uses an {@link UndertowDispatcher}. */
public final class UndertowDispatcherHandler implements HttpHandler {

    public static HttpHandler create() {
        return new UndertowDispatcherHandler();
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        Sender.Value<String> sender = UndertowSender.JsonValue.create(exchange);
        Dispatcher dispatcher = UndertowDispatcher.create(exchange);
        if (!dispatcher.isInIoThread()) {
            sender.sendErrorCode(418);
            return;
        }

        switch (exchange.getRequestPath()) {
            case "/dispatch/ok" -> dispatcher.dispatch(sender, this::workerHandler);
            case "/dispatch/error" -> dispatcher.dispatch(sender, this::badHandler);
            case "/dispatched/ok/worker" -> dispatchManually(
                    dispatcher.getWorker(), sender, dispatcher, this::workerHandler);
            case "/dispatched/ok/io-thread" -> dispatchManually(
                    dispatcher.getIoThread(), sender, dispatcher, this::ioThreadHandler);
            case "/dispatched/error" -> dispatchManually(dispatcher.getWorker(), sender, dispatcher, this::badHandler);
            default -> sender.sendErrorCode(StatusCodes.NOT_FOUND);
        }
    }

    private void dispatchManually(
            Executor executor,
            Sender.Value<String> sender,
            Dispatcher dispatcher,
            ApiHandler.ZeroArg<Sender.Value<String>> handler) {
        Dispatcher.DispatchedHandler dispatchedHandler = () -> handler.handleRequest(sender, dispatcher);
        executor.execute(() -> dispatcher.executeHandler(dispatchedHandler));
        dispatcher.dispatched();
    }

    private void workerHandler(Sender.Value<String> sender, Dispatcher dispatcher) {
        if (dispatcher.isInIoThread()) {
            sender.sendErrorCode(418);
            return;
        }

        sender.sendValue("test");
    }

    private void ioThreadHandler(Sender.Value<String> sender, Dispatcher dispatcher) {
        if (!dispatcher.isInIoThread()) {
            sender.sendErrorCode(418);
            return;
        }

        sender.sendValue("test");
    }

    private void badHandler(Sender.Value<String> sender, Dispatcher dispatcher) {
        throw new RuntimeException();
    }

    private UndertowDispatcherHandler() {}
}
