package org.example.age.infra.service.client;

import org.example.age.api.Dispatcher;
import org.example.age.api.HttpOptional;
import org.example.age.api.Sender;

/** Callback for a backend request made as part of a frontend exchange, where the response has a JSON body. */
@FunctionalInterface
public interface ResponseJsonCallback<S extends Sender, V> {

    void onResponse(S sender, HttpOptional<V> maybeValue, Dispatcher dispatcher) throws Exception;
}
