package org.example.age.common.client;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Proxies the request to another server using a {@link RequestDispatcher}.
 *
 * <p>Also contains a special exception trigger.</p>
 */
@Singleton
final class TestProxyHandler implements HttpHandler {

    private final RequestDispatcher requestDispatcher;
    private final Supplier<String> backendUrlSupplier;

    @Inject
    TestProxyHandler(RequestDispatcher requestDispatcher, @Named("backendUrl") Supplier<String> backendUrlSupplier) {
        this.requestDispatcher = requestDispatcher;
        this.backendUrlSupplier = backendUrlSupplier;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        Request request = new Request.Builder().url(backendUrlSupplier.get()).build();
        requestDispatcher.dispatch(request, exchange, TestProxyHandler::sendResponse);
    }

    private static void sendResponse(Response response, byte[] responseBody, HttpServerExchange exchange) {
        String text = new String(responseBody, StandardCharsets.UTF_8);
        if (text.equals("error")) {
            throw new RuntimeException("error triggered");
        }

        exchange.setStatusCode(response.code());
        exchange.getResponseSender().send(text);
    }
}
