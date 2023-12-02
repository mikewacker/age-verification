package org.example.age.infra.api;

import io.undertow.server.HttpServerExchange;
import java.util.concurrent.atomic.AtomicBoolean;
import org.example.age.api.StatusCodeSender;

/** {@link StatusCodeSender} that is backed by an {@link HttpServerExchange}. */
public final class ExchangeStatusCodeSender implements StatusCodeSender {

    private final HttpServerExchange exchange;
    private final AtomicBoolean wasSent = new AtomicBoolean(false);

    /** Creates the {@link StatusCodeSender} from the {@link HttpServerExchange}. */
    public static StatusCodeSender create(HttpServerExchange exchange) {
        return new ExchangeStatusCodeSender(exchange);
    }

    @Override
    public void send(int statusCode) {
        if (wasSent.getAndSet(true)) {
            return;
        }

        exchange.setStatusCode(statusCode);
        exchange.endExchange();
    }

    private ExchangeStatusCodeSender(HttpServerExchange exchange) {
        this.exchange = exchange;
    }
}
