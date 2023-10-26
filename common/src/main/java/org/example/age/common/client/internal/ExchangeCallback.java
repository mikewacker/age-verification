package org.example.age.common.client.internal;

import io.undertow.server.HttpServerExchange;
import okhttp3.Response;

/**
 * Callback for a backend request made as part of a frontend exchange,
 * with the response body already read for a successful response, or set to null for an unsuccessful response.
 *
 * <p>The {@code Void} type can be used if the response does not have a body.</p>
 */
@FunctionalInterface
public interface ExchangeCallback<T> {

    void onResponse(Response response, T responseBody, HttpServerExchange exchange) throws Exception;
}
