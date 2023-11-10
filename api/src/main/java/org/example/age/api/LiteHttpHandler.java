package org.example.age.api;

/** Lightweight HTTP handler that can dispatch requests and send a response. */
@FunctionalInterface
public interface LiteHttpHandler<S extends Sender> {

    void handleRequest(S sender, Dispatcher executors) throws Exception;
}
