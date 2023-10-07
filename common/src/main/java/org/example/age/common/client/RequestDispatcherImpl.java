package org.example.age.common.client;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.SameThreadExecutor;
import io.undertow.util.StatusCodes;
import java.io.IOException;
import javax.inject.Inject;
import javax.inject.Singleton;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import org.xnio.IoUtils;

@Singleton
final class RequestDispatcherImpl implements RequestDispatcher {

    private final ExchangeClient client;

    @Inject
    public RequestDispatcherImpl(ExchangeClient client) {
        this.client = client;
    }

    @Override
    public void dispatch(Request request, HttpServerExchange exchange, ExchangeCallback callback) {
        Call call = client.getInstance(exchange).newCall(request);
        Callback adaptedCallback = AdaptedCallback.create(exchange, callback);
        exchange.dispatch(SameThreadExecutor.INSTANCE, () -> call.enqueue(adaptedCallback));
    }

    /** Adapts a {@link Callback} to an {@link ExchangeCallback}. */
    private static final class AdaptedCallback implements Callback {

        private final HttpServerExchange exchange;
        private final ExchangeCallback callback;

        /** Creates an adapted callback from the exchange and the callback. */
        public static Callback create(HttpServerExchange exchange, ExchangeCallback callback) {
            return new AdaptedCallback(exchange, callback);
        }

        @Override
        public void onResponse(Call call, Response response) {
            byte[] responseBody;
            try {
                responseBody = response.body().bytes();
            } catch (IOException e) {
                handleFailure();
                return;
            }

            try {
                callback.onResponse(response, responseBody, exchange);
            } catch (Exception e) {
                handleUncaughtException();
            }
        }

        @Override
        public void onFailure(Call call, IOException e) {
            handleFailure();
        }

        /** Handles a failure by sending a 502 error. */
        private void handleFailure() {
            exchange.setStatusCode(StatusCodes.BAD_GATEWAY);
            exchange.endExchange();
        }

        /** Handles an uncaught exception by sending a 500 error. */
        private void handleUncaughtException() {
            if (exchange.isResponseStarted()) {
                IoUtils.safeClose(exchange.getConnection());
                return;
            }

            exchange.setStatusCode(StatusCodes.INTERNAL_SERVER_ERROR);
            exchange.endExchange();
        }

        private AdaptedCallback(HttpServerExchange exchange, ExchangeCallback callback) {
            this.exchange = exchange;
            this.callback = callback;
        }
    }
}
