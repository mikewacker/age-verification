package org.example.age.api.infra;

import com.fasterxml.jackson.core.type.TypeReference;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import org.example.age.api.base.Dispatcher;
import org.example.age.api.base.Sender;

/**
 * Test {@link HttpHandler} that uses a {@link UndertowJsonApiHandler}.
 *
 * <p>It adds two numbers: one in the body, and one in a query parameter. It also contains a health check.</p>
 *
 * <p>Also triggers an error if the sum is 500.</p>
 */
public final class AddHandler implements HttpHandler {

    private final HttpHandler addHandler;
    private final HttpHandler healthHandler;
    private final HttpHandler notFoundHandler;

    public static HttpHandler create() {
        return new AddHandler();
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        switch (exchange.getRequestPath()) {
            case "/add" -> addHandler.handleRequest(exchange);
            case "/health" -> healthHandler.handleRequest(exchange);
            default -> notFoundHandler.handleRequest(exchange);
        }
    }

    private AddHandler() {
        addHandler = UndertowJsonApiHandler.builder(new TypeReference<Integer>() {})
                .addBody(new TypeReference<Integer>() {})
                .addQueryParam("operand", new TypeReference<Integer>() {})
                .build(this::handleAddRequest);
        healthHandler = UndertowJsonApiHandler.builder().build(this::handleHealthRequest);
        notFoundHandler = UndertowJsonApiHandler.notFound();
    }

    private void handleAddRequest(Sender.Value<Integer> sender, int operand1, int operand2, Dispatcher dispatcher) {
        int sum = operand1 + operand2;
        if (sum == 500) {
            throw new RuntimeException();
        }

        sender.sendValue(sum);
    }

    private void handleHealthRequest(Sender.StatusCode sender, Dispatcher dispatcher) {
        sender.sendOk();
    }
}
