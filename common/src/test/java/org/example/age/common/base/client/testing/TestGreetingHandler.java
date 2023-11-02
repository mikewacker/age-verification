package org.example.age.common.base.client.testing;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.SameThreadExecutor;
import io.undertow.util.StatusCodes;
import java.io.IOException;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import org.example.age.common.base.client.internal.ExchangeClient;
import org.example.age.common.base.utils.internal.ExchangeUtils;

/** Sends a greeting, making a backend request via {@link ExchangeClient} to get the recipient. */
@Singleton
public final class TestGreetingHandler implements HttpHandler {

    private final ExchangeClient client;
    private final Provider<String> backendUrlProvider;

    @Inject
    public TestGreetingHandler(ExchangeClient client, @Named("backendUrl") Provider<String> backendUrlProvider) {
        this.client = client;
        this.backendUrlProvider = backendUrlProvider;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        Request request = new Request.Builder().url(backendUrlProvider.get()).build();
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
                ExchangeUtils.sendStatusCode(exchange, StatusCodes.BAD_GATEWAY);
                return;
            }

            String greeting = String.format("Hello, %s!", recipient);
            exchange.getResponseSender().send(greeting);
        }

        @Override
        public void onFailure(Call call, IOException e) {
            ExchangeUtils.sendStatusCode(exchange, StatusCodes.BAD_GATEWAY);
        }

        private GreetingCallback(HttpServerExchange exchange) {
            this.exchange = exchange;
        }
    }
}
