package org.example.age.client;

import io.undertow.server.HttpServerExchange;
import okhttp3.Response;

/** Callback for a frontend exchange that made a backend request and read the body of a successful response. */
@FunctionalInterface
public interface SuccessCallback {

    void onSuccess(Response response, byte[] responseBody, HttpServerExchange exchange) throws Exception;
}
