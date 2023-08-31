package org.example.age.client;

import io.undertow.server.HttpServerExchange;
import okhttp3.Request;

/**
 * Dispatches an HTTP request to a backend server as part of a frontend exchange.
 *
 * <p>The frontend server will send a 5xx error code if it does not receive a successful backend response;
 * the caller only needs to specify how to handle a successful backend response.</p>
 */
@FunctionalInterface
public interface RequestDispatcher {

    void dispatch(Request request, SuccessCallback callback, HttpServerExchange exchange);
}
