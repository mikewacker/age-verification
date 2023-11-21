package org.example.age.infra.api;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;
import org.example.age.api.HttpOptional;
import org.example.age.api.JsonSender;
import org.example.age.api.JsonSerializer;

/** {@link JsonSender} that is backed by an {@link HttpServerExchange}. */
public final class ExchangeJsonSender<B> implements JsonSender<B> {

    private final HttpServerExchange exchange;
    private final JsonSerializer serializer;
    private final AtomicBoolean wasSent = new AtomicBoolean(false);

    /** Creates the {@link JsonSender} from the {@link HttpServerExchange}. */
    public static <B> JsonSender<B> create(HttpServerExchange exchange, JsonSerializer serializer) {
        return new ExchangeJsonSender<>(exchange, serializer);
    }

    @Override
    public void send(HttpOptional<B> maybeBody) {
        if (wasSent.getAndSet(true)) {
            return;
        }

        if (maybeBody.isEmpty()) {
            sendErrorCode(maybeBody.statusCode());
            return;
        }

        B body = maybeBody.get();
        byte[] rawBody = serializer.serialize(body);
        sendRawBody(rawBody);
    }

    /** Sends the raw body. */
    private void sendRawBody(byte[] rawBody) {
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send(ByteBuffer.wrap(rawBody));
    }

    /** Sends an error status code. */
    private void sendErrorCode(int errorCode) {
        exchange.setStatusCode(errorCode);
        exchange.endExchange();
    }

    private ExchangeJsonSender(HttpServerExchange exchange, JsonSerializer serializer) {
        this.exchange = exchange;
        this.serializer = serializer;
    }
}
