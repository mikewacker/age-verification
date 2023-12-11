package org.example.age.api.infra;

import io.undertow.server.HttpServerExchange;
import org.example.age.api.base.HttpOptional;
import org.example.age.api.base.ValueSender;

/** JSON {@link ValueSender} that is backed by an Undertow {@link HttpServerExchange}. */
final class UndertowJsonValueSender<V> implements ValueSender<V> {

    private final HttpServerExchange exchange;

    /** Creates a {@link ValueSender} from an {@link HttpServerExchange}. */
    public static <V> ValueSender<V> create(HttpServerExchange exchange) {
        return new UndertowJsonValueSender<>(exchange);
    }

    @Override
    public void send(HttpOptional<V> maybeValue) {
        if (maybeValue.isEmpty()) {
            UndertowResponse.sendStatusCode(exchange, maybeValue.statusCode());
            return;
        }
        V value = maybeValue.get();

        UndertowResponse.sendJsonValue(exchange, value);
    }

    private UndertowJsonValueSender(HttpServerExchange exchange) {
        this.exchange = exchange;
    }
}
