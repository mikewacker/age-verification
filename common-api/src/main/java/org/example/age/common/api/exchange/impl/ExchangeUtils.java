package org.example.age.common.api.exchange.impl;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;
import org.example.age.common.api.HttpOptional;

/** Utilities for handling an {@link HttpServerExchange}. */
public final class ExchangeUtils {

    private static final Deque<String> EMPTY_VALUES = new ArrayDeque<>();

    /** Handles a request with a body. */
    public static <T> void handleRequestWithBody(
            HttpServerExchange exchange, BytesDeserializer<T> deserializer, RequestBodyCallback<T> handler) {
        exchange.getRequestReceiver()
                .receiveFullBytes(
                        (ex, rawRequestBody) -> onRequestBodyReceived(exchange, rawRequestBody, deserializer, handler));
    }

    /** Gets the (first) value of a query parameter, if present. */
    public static Optional<String> tryGetQueryParameter(HttpServerExchange exchange, String name) {
        Deque<String> values = exchange.getQueryParameters().getOrDefault(name, EMPTY_VALUES);
        return !values.isEmpty() ? Optional.of(values.getFirst()) : Optional.empty();
    }

    /**
     * Gets the (first) value of a query parameter, if present.
     *
     * <p>Also returns empty if deserialization fails.</p>
     */
    public static <T> Optional<T> tryGetQueryParameter(
            HttpServerExchange exchange, String name, TextDeserializer<T> deserializer) {
        Optional<String> maybeValue = tryGetQueryParameter(exchange, name);
        if (maybeValue.isEmpty()) {
            return Optional.empty();
        }

        String value = maybeValue.get();
        try {
            T t = deserializer.deserialize(value);
            return Optional.of(t);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /** Completes the exchange by sending only a status code. */
    public static void sendStatusCode(HttpServerExchange exchange, int statusCode) {
        exchange.setStatusCode(statusCode);
        exchange.endExchange();
    }

    /** Completes the exchange by sending a response body. */
    public static <T> void sendResponseBody(
            HttpServerExchange exchange, String contentType, T responseBody, BytesSerializer<T> serializer) {
        byte[] rawResponseBody;
        try {
            rawResponseBody = serializer.serialize(responseBody);
        } catch (Exception e) {
            sendStatusCode(exchange, StatusCodes.INTERNAL_SERVER_ERROR);
            return;
        }

        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, contentType);
        exchange.getResponseSender().send(ByteBuffer.wrap(rawResponseBody));
    }

    /** Completes the exchange by sending a response body or an error status code. */
    public static <T> void sendResponse(
            HttpServerExchange exchange,
            String contentType,
            HttpOptional<T> maybeResponseBody,
            BytesSerializer<T> serializer) {
        if (maybeResponseBody.isEmpty()) {
            sendStatusCode(exchange, maybeResponseBody.statusCode());
            return;
        }

        T responseBody = maybeResponseBody.get();
        sendResponseBody(exchange, contentType, responseBody, serializer);
    }

    /** Called when the request body is received. */
    private static <T> void onRequestBodyReceived(
            HttpServerExchange exchange,
            byte[] rawRequestBody,
            BytesDeserializer<T> deserializer,
            RequestBodyCallback<T> handler) {
        T requestBody;
        try {
            requestBody = deserializer.deserialize(rawRequestBody);
        } catch (Exception e) {
            sendStatusCode(exchange, StatusCodes.BAD_REQUEST);
            return;
        }

        // FullBytesCallback does not throw an exception, so we have to handle any exceptions here.
        try {
            handler.handleRequest(exchange, requestBody);
        } catch (Exception e) {
            sendStatusCode(exchange, StatusCodes.INTERNAL_SERVER_ERROR);
        }
    }

    // static class
    private ExchangeUtils() {}
}
