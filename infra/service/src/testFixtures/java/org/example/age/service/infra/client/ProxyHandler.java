package org.example.age.service.infra.client;

import com.fasterxml.jackson.core.type.TypeReference;
import dagger.Component;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import javax.inject.Singleton;
import org.example.age.api.base.Dispatcher;
import org.example.age.api.base.HttpOptional;
import org.example.age.api.base.Sender;
import org.example.age.api.infra.UndertowJsonApiHandler;
import org.example.age.testing.server.TestServer;

/**
 * Test {@link HttpHandler} that uses a {@link RequestDispatcher}.
 *
 * <p>It proxies the response it receives from a backend server, supporting both a status code and text.</p>
 */
public final class ProxyHandler implements HttpHandler {

    private final HttpHandler statusCodeHandler;
    private final HttpHandler textHandler;
    private final HttpHandler notFoundHandler;

    private final RequestDispatcher requestDispatcher;
    private final TestServer<?> backendServer;

    public static HttpHandler create() {
        return new ProxyHandler();
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        switch (exchange.getRequestPath()) {
            case "/status-code" -> statusCodeHandler.handleRequest(exchange);
            case "/text" -> textHandler.handleRequest(exchange);
            default -> notFoundHandler.handleRequest(exchange);
        }
    }

    private ProxyHandler() {
        statusCodeHandler = UndertowJsonApiHandler.builder().build(this::handleStatusCodeRequest);
        textHandler =
                UndertowJsonApiHandler.builder(new TypeReference<String>() {}).build(this::handleTextRequest);
        notFoundHandler = UndertowJsonApiHandler.notFound();

        requestDispatcher = TestComponent.createRequestDispatcher();
        backendServer = TestServer.get("backend");
    }

    private void handleStatusCodeRequest(Sender.StatusCode sender, Dispatcher dispatcher) {
        requestDispatcher
                .requestBuilder(dispatcher)
                .get(backendServer.rootUrl())
                .dispatch(sender, this::handleStatusCodeResponse);
    }

    /** Callback for a backend request whose response is a status code. */
    private void handleStatusCodeResponse(Sender.StatusCode sender, int statusCode, Dispatcher dispatcher) {
        sender.send(statusCode);
    }

    private void handleTextRequest(Sender.Value<String> sender, Dispatcher dispatcher) {
        requestDispatcher
                .requestBuilder(dispatcher, new TypeReference<String>() {})
                .get(backendServer.rootUrl())
                .dispatch(sender, this::handleTextResponse);
    }

    /** Callback for a backend request whose response is text. */
    private void handleTextResponse(
            Sender.Value<String> sender, HttpOptional<String> maybeText, Dispatcher dispatcher) {
        sender.send(maybeText);
    }

    /** Dagger component that provides a {@link RequestDispatcher}. */
    @Component(modules = RequestDispatcherModule.class)
    @Singleton
    interface TestComponent {

        static RequestDispatcher createRequestDispatcher() {
            TestComponent component = DaggerProxyHandler_TestComponent.create();
            return component.requestDispatcher();
        }

        RequestDispatcher requestDispatcher();
    }
}
