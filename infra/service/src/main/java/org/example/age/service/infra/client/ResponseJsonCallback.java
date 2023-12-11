package org.example.age.service.infra.client;

import org.example.age.api.base.Dispatcher;
import org.example.age.api.base.HttpOptional;
import org.example.age.api.base.Sender;

/** Callback for a backend request made as part of a frontend exchange, where the response has a JSON body. */
@FunctionalInterface
public interface ResponseJsonCallback<S extends Sender, V> {

    void onResponse(S sender, HttpOptional<V> maybeValue, Dispatcher dispatcher) throws Exception;
}
