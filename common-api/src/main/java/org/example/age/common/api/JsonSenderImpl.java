package org.example.age.common.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

final class JsonSenderImpl<B> implements JsonSender<B> {

    private final HttpServerExchange exchange;
    private final ObjectMapper mapper;
    private final AtomicBoolean wasSent = new AtomicBoolean(false);

    public JsonSenderImpl(HttpServerExchange exchange, ObjectMapper mapper) {
        this.exchange = exchange;
        this.mapper = mapper;
    }

    @Override
    public void send(HttpOptional<B> maybeBody) {
        if (wasSent.getAndSet(true)) {
            return;
        }

        if (maybeBody.isEmpty()) {
            sendStatusCode(maybeBody.statusCode());
            return;
        }

        B body = maybeBody.get();
        byte[] rawBody;
        try {
            rawBody = mapper.writeValueAsBytes(body);
        } catch (JsonProcessingException e) {
            sendStatusCode(StatusCodes.INTERNAL_SERVER_ERROR);
            return;
        }

        sendRawBody(rawBody);
    }

    private void sendRawBody(byte[] rawBody) {
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send(ByteBuffer.wrap(rawBody));
    }

    private void sendStatusCode(int statusCode) {
        exchange.setStatusCode(statusCode);
        exchange.endExchange();
    }
}
