package org.example.age.infra.service.client;

import okhttp3.Response;
import org.example.age.api.Dispatcher;
import org.example.age.api.Sender;

/** Callback for a backend request made as part of a frontend exchange, where the response does not have a body. */
@FunctionalInterface
public interface ResponseCallback<S extends Sender> {

    void onResponse(Response response, S sender, Dispatcher dispatcher) throws Exception;
}
