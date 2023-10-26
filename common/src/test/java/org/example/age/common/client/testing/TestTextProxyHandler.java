package org.example.age.common.client.testing;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import okhttp3.Request;
import okhttp3.Response;
import org.example.age.common.client.internal.RequestDispatcher;
import org.example.age.common.utils.internal.ExchangeUtils;

/**
 * Proxies the request to another server using a {@link RequestDispatcher}.
 *
 * <p>Also contains special exception triggers.</p>
 */
@Singleton
public final class TestTextProxyHandler implements HttpHandler {

    private final RequestDispatcher requestDispatcher;
    private final Supplier<String> backendUrlSupplier;

    @Inject
    TestTextProxyHandler(
            RequestDispatcher requestDispatcher, @Named("backendUrl") Supplier<String> backendUrlSupplier) {
        this.requestDispatcher = requestDispatcher;
        this.backendUrlSupplier = backendUrlSupplier;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        Request request = new Request.Builder().url(backendUrlSupplier.get()).build();
        requestDispatcher.dispatchWithResponseBody(
                request, exchange, TestTextProxyHandler::deserialize, TestTextProxyHandler::sendResponse);
    }

    private static void sendResponse(Response response, String text, HttpServerExchange exchange) {
        if (!response.isSuccessful()) {
            ExchangeUtils.sendStatusCode(exchange, response.code());
            return;
        }

        if (text.equals("callback error")) {
            throw new RuntimeException();
        }

        ExchangeUtils.sendResponseBody(exchange, "text/plain", text, TestTextProxyHandler::serialize);
    }

    private static byte[] serialize(String text) {
        return text.getBytes(StandardCharsets.UTF_8);
    }

    private static String deserialize(byte[] bytes) {
        String text = new String(bytes, StandardCharsets.UTF_8);
        if (text.equals("deserialize error")) {
            throw new RuntimeException();
        }

        return text;
    }
}
