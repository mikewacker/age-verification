package org.example.age.infra.service.client;

import okhttp3.Response;
import org.example.age.api.Dispatcher;
import org.example.age.api.Sender;

/**
 * Callback for a backend request made as part of a frontend exchange, where the response has a body.
 *
 * <p>The body will be null if the response is not successful.</p>
 */
@FunctionalInterface
public interface ResponseBodyCallback<B, S extends Sender> {

    void onResponse(Response response, B responseBody, S sender, Dispatcher dispatcher) throws Exception;
}
