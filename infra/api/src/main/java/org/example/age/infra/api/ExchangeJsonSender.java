package org.example.age.infra.api;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;
import org.example.age.api.base.HttpOptional;
import org.example.age.api.base.ValueSender;
import org.example.age.data.json.JsonValues;

/** {@link ValueSender} that is backed by an {@link HttpServerExchange}. */
public final class ExchangeJsonSender<V> implements ValueSender<V> {

    private final HttpServerExchange exchange;
    private final AtomicBoolean wasSent = new AtomicBoolean(false);

    /** Creates the {@link ValueSender} from the {@link HttpServerExchange}. */
    public static <V> ValueSender<V> create(HttpServerExchange exchange) {
        return new ExchangeJsonSender<>(exchange);
    }

    @Override
    public void send(HttpOptional<V> maybeValue) {
        if (wasSent.getAndSet(true)) {
            return;
        }

        if (maybeValue.isEmpty()) {
            sendErrorCodeInternal(maybeValue.statusCode());
            return;
        }

        V value = maybeValue.get();
        sendValueInternal(value);
    }

    /** Sends a JSON body. */
    private void sendValueInternal(Object value) {
        byte[] rawValue = JsonValues.serialize(value);
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send(ByteBuffer.wrap(rawValue));
    }

    /** Sends an error status code. */
    private void sendErrorCodeInternal(int errorCode) {
        exchange.setStatusCode(errorCode);
        exchange.endExchange();
    }

    private ExchangeJsonSender(HttpServerExchange exchange) {
        this.exchange = exchange;
    }
}
