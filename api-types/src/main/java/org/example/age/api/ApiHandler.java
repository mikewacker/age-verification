package org.example.age.api;

/** API handler that can send a response or dispatch the request. */
@FunctionalInterface
public interface ApiHandler<S extends Sender> {

    void handleRequest(S sender, Dispatcher dispatcher) throws Exception;
}
