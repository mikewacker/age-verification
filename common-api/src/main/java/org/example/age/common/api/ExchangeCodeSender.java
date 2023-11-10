package org.example.age.common.api;

import io.undertow.server.HttpServerExchange;
import java.util.concurrent.atomic.AtomicBoolean;
import org.example.age.api.CodeSender;

/** {@link CodeSender} that is backed by an {@link HttpServerExchange}. */
public final class ExchangeCodeSender implements CodeSender {

    private final HttpServerExchange exchange;
    private final AtomicBoolean wasSent = new AtomicBoolean(false);

    /** Creates the {@link CodeSender} from the {@link HttpServerExchange}. */
    public static CodeSender create(HttpServerExchange exchange) {
        return new ExchangeCodeSender(exchange);
    }

    @Override
    public void send(int statusCode) {
        if (wasSent.getAndSet(true)) {
            return;
        }

        exchange.setStatusCode(statusCode);
        exchange.endExchange();
    }

    private ExchangeCodeSender(HttpServerExchange exchange) {
        this.exchange = exchange;
    }
}
