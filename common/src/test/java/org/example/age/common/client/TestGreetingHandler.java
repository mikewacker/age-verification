package org.example.age.common.client;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.SameThreadExecutor;
import io.undertow.util.StatusCodes;
import java.io.IOException;
import java.util.function.Supplier;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/** Sends a greeting, making a backend request via {@link ExchangeClient} to get the recipient. */
@Singleton
final class TestGreetingHandler implements HttpHandler {

    private final ExchangeClient client;
    private final Supplier<String> backendUrlSupplier;

    @Inject
    public TestGreetingHandler(ExchangeClient client, @Named("backendUrl") Supplier<String> backendUrlSupplier) {
        this.client = client;
        this.backendUrlSupplier = backendUrlSupplier;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        Request request = new Request.Builder().url(backendUrlSupplier.get()).build();
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
            exchange.getResponseSender().send(greeting);
        }

        @Override
        public void onFailure(Call call, IOException e) {
            sendError();
        }

        /** Sends a 502 error. */
        private void sendError() {
            exchange.setStatusCode(StatusCodes.BAD_GATEWAY);
            exchange.endExchange();
        }

        private GreetingCallback(HttpServerExchange exchange) {
            this.exchange = exchange;
        }
    }
}
