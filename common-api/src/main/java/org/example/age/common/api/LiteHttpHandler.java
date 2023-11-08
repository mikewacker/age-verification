package org.example.age.common.api;

/** Lightweight HTTP handler that can dispatch requests and send a response. */
@FunctionalInterface
public interface LiteHttpHandler<S extends Sender> {

    void handleRequest(ExchangeExecutors executors, S sender) throws Exception;
}
