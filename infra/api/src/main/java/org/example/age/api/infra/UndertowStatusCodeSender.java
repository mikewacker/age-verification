package org.example.age.api.infra;

import io.undertow.server.HttpServerExchange;
import org.example.age.api.base.StatusCodeSender;

/** {@link StatusCodeSender} that is backed by an Undertow {@link HttpServerExchange}. */
final class UndertowStatusCodeSender implements StatusCodeSender {

    private final HttpServerExchange exchange;

    /** Creates a {@link StatusCodeSender} from an {@link HttpServerExchange}. */
    public static StatusCodeSender create(HttpServerExchange exchange) {
        return new UndertowStatusCodeSender(exchange);
    }

    @Override
    public void send(int statusCode) {
        UndertowResponse.sendStatusCode(exchange, statusCode);
    }

    private UndertowStatusCodeSender(HttpServerExchange exchange) {
        this.exchange = exchange;
    }
}
