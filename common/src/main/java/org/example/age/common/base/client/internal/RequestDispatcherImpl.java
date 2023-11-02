package org.example.age.common.base.client.internal;

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
import org.example.age.common.base.utils.internal.BytesDeserializer;
import org.example.age.common.base.utils.internal.ExchangeUtils;
import org.xnio.IoUtils;

@Singleton
final class RequestDispatcherImpl implements RequestDispatcher {

    private final ExchangeClient client;

    @Inject
    public RequestDispatcherImpl(ExchangeClient client) {
        this.client = client;
    }

    @Override
    public <T> void dispatchWithResponseBody(
            Request request,
            HttpServerExchange exchange,
            BytesDeserializer<T> deserializer,
            ExchangeCallback<T> callback) {
        Call call = client.getInstance(exchange).newCall(request);
        Callback adaptedCallback = AdaptedCallback.create(exchange, deserializer, callback);
        exchange.dispatch(SameThreadExecutor.INSTANCE, () -> call.enqueue(adaptedCallback));
    }

    /** Adapts a {@link Callback} to an {@link ExchangeCallback}. */
    private static final class AdaptedCallback<T> implements Callback {

        private final HttpServerExchange exchange;
        private final BytesDeserializer<T> deserializer;
        private final ExchangeCallback<T> callback;

        /** Creates an adapted callback from the exchange and the callback. */
        public static <T> Callback create(
                HttpServerExchange exchange, BytesDeserializer<T> deserializer, ExchangeCallback<T> callback) {
            return new AdaptedCallback(exchange, deserializer, callback);
        }

        @Override
        public void onResponse(Call call, Response response) {
            T responseBody = null;
            try {
                if (response.isSuccessful()) {
                    byte[] rawResponseBody = response.body().bytes();
                    responseBody = deserializer.deserialize(rawResponseBody);
                }
            } catch (Exception e) {
                ExchangeUtils.sendStatusCode(exchange, StatusCodes.BAD_GATEWAY);
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
            ExchangeUtils.sendStatusCode(exchange, StatusCodes.BAD_GATEWAY);
        }

        /** Handles an uncaught exception by sending a 500 error. */
        private void handleUncaughtException() {
            if (exchange.isResponseStarted()) {
                IoUtils.safeClose(exchange.getConnection());
                return;
            }

            ExchangeUtils.sendStatusCode(exchange, StatusCodes.INTERNAL_SERVER_ERROR);
        }

        private AdaptedCallback(
                HttpServerExchange exchange, BytesDeserializer<T> deserializer, ExchangeCallback<T> callback) {
            this.exchange = exchange;
            this.deserializer = deserializer;
            this.callback = callback;
        }
    }
}
