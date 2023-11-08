package org.example.age.common.api;

import io.undertow.server.HttpServerExchange;
import java.util.concurrent.atomic.AtomicBoolean;

final class CodeSenderImpl implements CodeSender {

    private final HttpServerExchange exchange;
    private final AtomicBoolean wasSent = new AtomicBoolean(false);

    public CodeSenderImpl(HttpServerExchange exchange) {
        this.exchange = exchange;
    }

    @Override
    public void send(int statusCode) {
        if (wasSent.getAndSet(true)) {
            return;
        }

        exchange.setStatusCode(statusCode);
        exchange.endExchange();
    }
}
