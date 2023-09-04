package org.example.age.common.client;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.SameThreadExecutor;
import io.undertow.util.StatusCodes;
import java.io.IOException;
import javax.inject.Inject;
import javax.inject.Singleton;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Test handler that sends either a greeting or the recipient of said greeting.
 *
 * <p>To send a greeting, the server makes an HTTP request to itself to get the recipient of said greeting.
 * The request uses an {@link HttpServerExchangeClient}.</p>
 */
@Singleton
final class TestGreetingHandler implements HttpHandler {

    HttpServerExchangeClient client;

    @Inject
    public TestGreetingHandler(HttpServerExchangeClient client) {
        this.client = client;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        if (exchange.getRequestPath().equals("/recipient")) {
            sendRecipient(exchange);
            return;
        }

        sendGreeting(exchange);
    }

    /** Sends the recipient of the greeting. */
    private void sendRecipient(HttpServerExchange exchange) {
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
        exchange.getResponseSender().send("world");
    }

    /** Sends the greeting. */
    private void sendGreeting(HttpServerExchange exchange) {
        String url = String.format("http://%s:%d/recipient", exchange.getHostName(), exchange.getHostPort());
        Request request = new Request.Builder().url(url).build();
        Call call = client.getInstance(exchange).newCall(request);
        Callback callback = GreetingCallback.create(exchange);
        exchange.dispatch(SameThreadExecutor.INSTANCE, () -> call.enqueue(callback));
    }

    /** Sends the greeting when the recipient has been received. */
    private static final class GreetingCallback implements Callback {

        private final HttpServerExchange exchange;

        /** Creates a callback. */
        public static Callback create(HttpServerExchange exchange) {
            return new GreetingCallback(exchange);
        }

        @Override
        public void onResponse(Call call, Response response) {
            String recipient;
            try {
                recipient = response.body().string();
            } catch (IOException e) {
                sendError();
                return;
            }

            String greeting = String.format("Hello, %s!", recipient);
            sendGreeting(greeting);
        }

        @Override
        public void onFailure(Call call, IOException e) {
            sendError();
        }

        /** Sends the greeting. */
        private void sendGreeting(String greeting) {
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
            exchange.getResponseSender().send(greeting);
        }

        /** Sends a 500 error. */
        private void sendError() {
            exchange.setStatusCode(StatusCodes.INTERNAL_SERVER_ERROR);
            exchange.endExchange();
        }

        private GreetingCallback(HttpServerExchange exchange) {
            this.exchange = exchange;
        }
    }
}
