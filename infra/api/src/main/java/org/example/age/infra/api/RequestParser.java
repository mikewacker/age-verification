package org.example.age.infra.api;

import com.fasterxml.jackson.core.type.TypeReference;
import io.undertow.io.Receiver;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import java.util.ArrayDeque;
import java.util.Deque;
import org.example.age.api.HttpOptional;
import org.example.age.api.JsonSerializer;
import org.xnio.IoUtils;

/** Parses API arguments from an HTTP request. */
public final class RequestParser {

    private static final Deque<String> EMPTY_VALUES = new ArrayDeque<>();

    private final HttpServerExchange exchange;

    /** Creates a {@link RequestParser} for the {@link HttpServerExchange}. */
    public static RequestParser create(HttpServerExchange exchange) {
        return new RequestParser(exchange);
    }

    /**
     * Asynchronously reads the JSON body.
     *
     * <p>Sends a 400 error if the body cannot be deserialized,
     * or a 500 error if the callback throws an uncaught exception.</p>
     */
    public <V> void readBody(TypeReference<V> valueTypeRef, RequestJsonCallback<V> callback) {
        Receiver.FullBytesCallback adaptedCallback = new AdaptedRequestJsonCallback<>(valueTypeRef, callback);
        exchange.getRequestReceiver().receiveFullBytes(adaptedCallback);
    }

    /**
     * Gets the (first) value of a query parameter, if present.
     *
     * <p>Returns a 400 error if the parameter is missing or cannot be deserialized.</p>
     */
    public <V> HttpOptional<V> tryGetQueryParameter(String name, TypeReference<V> valueTypeRef) {
        HttpOptional<String> maybeRawValue = tryGetQueryParameter(name);
        if (maybeRawValue.isEmpty()) {
            return HttpOptional.empty(maybeRawValue.statusCode());
        }

        String rawValue = maybeRawValue.get();
        byte[] json = JsonSerializer.serialize(rawValue);
        return JsonSerializer.tryDeserialize(json, valueTypeRef, StatusCodes.BAD_REQUEST);
    }

    /**
     * Gets the (first) value of a query parameter, if present.
     *
     * <p>Returns a 400 error if the parameter is missing.</p>
     */
    public HttpOptional<String> tryGetQueryParameter(String name) {
        Deque<String> values = exchange.getQueryParameters().getOrDefault(name, EMPTY_VALUES);
        if (values.isEmpty()) {
            return HttpOptional.empty(StatusCodes.BAD_REQUEST);
        }

        return HttpOptional.of(values.getFirst());
    }

    private RequestParser(HttpServerExchange exchange) {
        this.exchange = exchange;
    }

    /** Adapts a {@link RequestJsonCallback} to a {@link Receiver.FullBytesCallback}. */
    private final class AdaptedRequestJsonCallback<V> implements Receiver.FullBytesCallback {

        private final TypeReference<V> valueTypeRef;
        private final RequestJsonCallback<V> callback;

        public AdaptedRequestJsonCallback(TypeReference<V> valueTypeRef, RequestJsonCallback<V> callback) {
            this.valueTypeRef = valueTypeRef;
            this.callback = callback;
        }

        @Override
        public void handle(HttpServerExchange exchange, byte[] rawValue) {
            HttpOptional<V> maybeValue = JsonSerializer.tryDeserialize(rawValue, valueTypeRef, StatusCodes.BAD_REQUEST);
            if (maybeValue.isEmpty()) {
                sendErrorCode(maybeValue.statusCode());
                return;
            }
            V value = maybeValue.get();

            // FullBytesCallback does not throw an exception, so we must handle exceptions here.
            try {
                callback.handleRequest(exchange, RequestParser.this, value);
            } catch (Exception e) {
                sendErrorCode(StatusCodes.INTERNAL_SERVER_ERROR);
            }
        }

        /** Sends an error status code. */
        private void sendErrorCode(int errorCode) {
            if (exchange.isResponseStarted()) {
                IoUtils.safeClose(exchange.getConnection());
                return;
            }

            exchange.setStatusCode(errorCode);
            exchange.endExchange();
        }
    }
}
