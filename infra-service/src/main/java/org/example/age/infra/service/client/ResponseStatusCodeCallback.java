package org.example.age.infra.service.client;

import org.example.age.api.Dispatcher;
import org.example.age.api.Sender;

/** Callback for a backend request made as part of a frontend exchange, where the response only has a status code. */
@FunctionalInterface
public interface ResponseStatusCodeCallback<S extends Sender> {

    void onResponse(S sender, int statusCode, Dispatcher dispatcher) throws Exception;
}
