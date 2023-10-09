package org.example.age.common.client.internal;

import io.undertow.server.HttpServerExchange;
import okhttp3.Response;

/** Callback for a backend request made as part of a frontend exchange, with the response body already read. */
@FunctionalInterface
public interface ExchangeCallback {

    void onResponse(Response response, byte[] responseBody, HttpServerExchange exchange) throws Exception;
}
