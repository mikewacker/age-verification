package org.example.age.infra.service.client;

import org.example.age.api.Dispatcher;
import org.example.age.api.Sender;

/**
 * Callback for a backend request made as part of a frontend exchange, where the response has a JSON body.
 *
 * <p>The body will be null if the status code is not successful.</p>
 */
@FunctionalInterface
public interface ResponseJsonCallback<S extends Sender, B> {

    void onResponse(S sender, int statusCode, B body, Dispatcher dispatcher) throws Exception;
}
