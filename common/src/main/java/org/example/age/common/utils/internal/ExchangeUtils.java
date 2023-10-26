package org.example.age.common.utils.internal;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;

/** Utilities for handling an {@link HttpServerExchange}. */
public final class ExchangeUtils {

    private static final Deque<String> EMPTY_VALUES = new ArrayDeque<>();

    /** Handles a request with a body. */
    public static <T> void handleRequestWithBody(
            HttpServerExchange exchange, Deserializer<T> deserializer, RequestBodyCallback<T> handler) {
        exchange.getRequestReceiver()
                .receiveFullBytes(
                        (ex, rawRequestBody) -> onRequestBodyReceived(exchange, rawRequestBody, deserializer, handler));
    }

    /** Gets the (first) value of a query parameter, if present. */
    public static Optional<String> tryGetQueryParameter(HttpServerExchange exchange, String name) {
        Deque<String> values = exchange.getQueryParameters().getOrDefault(name, EMPTY_VALUES);
        return !values.isEmpty() ? Optional.of(values.getFirst()) : Optional.empty();
    }

    /** Completes the exchange by sending only a status code. */
    public static void sendStatusCode(HttpServerExchange exchange, int statusCode) {
        exchange.setStatusCode(statusCode);
        exchange.endExchange();
    }

    /** Completes the exchange by sending a response body. */
    public static <T> void sendResponseBody(
            HttpServerExchange exchange, String contentType, T responseBody, Serializer<T> serializer) {
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, contentType);
        ByteBuffer rawResponseBody = ByteBuffer.wrap(serializer.serialize(responseBody));
        exchange.getResponseSender().send(rawResponseBody);
    }

    /** Completes the exchange by sending a response body or an error status code. */
    public static <T> void sendResponse(
            HttpServerExchange exchange,
            String contentType,
            HttpOptional<T> maybeResponseBody,
            Serializer<T> serializer) {
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
            Deserializer<T> deserializer,
            RequestBodyCallback<T> handler) {
        T requestBody;
        try {
            requestBody = deserializer.deserialize(rawRequestBody);
        } catch (Exception e) {
            sendStatusCode(exchange, StatusCodes.BAD_REQUEST);
            return;
        }

        try {
            handler.handleRequest(exchange, requestBody);
        } catch (Exception e) {
            sendStatusCode(exchange, StatusCodes.INTERNAL_SERVER_ERROR);
        }
    }

    // static class
    private ExchangeUtils() {}
}
