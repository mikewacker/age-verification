package org.example.age.common.base.client.internal;

import io.undertow.server.HttpServerExchange;
import okhttp3.Request;
import org.example.age.common.api.exchange.impl.BytesDeserializer;

/**
 * Dispatches an HTTP request to a backend server as part of a frontend exchange.
 *
 * <p>Failures for the backend request are not handled by the callback; they will result in a 502 (Bad Gateway) error.
 * Uncaught exceptions thrown by the callback will result in a 500 (Internal Server Error) error.</p>
 */
@FunctionalInterface
public interface RequestDispatcher {

    <T> void dispatchWithResponseBody(
            Request request,
            HttpServerExchange exchange,
            BytesDeserializer<T> deserializer,
            ExchangeCallback<T> callback);

    default void dispatchWithoutResponseBody(
            Request request, HttpServerExchange exchange, ExchangeCallback<Void> callback) {
        dispatchWithResponseBody(request, exchange, bytes -> null, callback);
    }
}
