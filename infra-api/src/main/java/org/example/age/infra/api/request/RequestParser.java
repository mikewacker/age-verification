package org.example.age.infra.api.request;

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
    private final JsonSerializer serializer;

    /** Creates a {@link RequestParser} for the {@link HttpServerExchange}. */
    public static RequestParser create(HttpServerExchange exchange, JsonSerializer serializer) {
        return new RequestParser(exchange, serializer);
    }

    /**
     * Asynchronously reads the JSON body.
     *
     * <p>Sends a 400 error if the body cannot be deserialized,
     * or a 500 error if the callback throws an uncaught exception.</p>
     */
    public <B> void readBody(TypeReference<B> bodyTypeRef, RequestBodyCallback<B> callback) {
        Receiver.FullBytesCallback adaptedCallback = new AdaptedRequestBodyCallback<>(bodyTypeRef, callback);
        exchange.getRequestReceiver().receiveFullBytes(adaptedCallback);
    }

    /**
     * Gets the (first) value of a query parameter, if present.
     *
     * <p>Returns a 400 error if the parameter is missing or cannot be deserialized.</p>
     */
    public <P> HttpOptional<P> tryGetQueryParameter(String name, TypeReference<P> paramTypeRef) {
        HttpOptional<String> maybeRawValue = tryGetQueryParameter(name);
        if (maybeRawValue.isEmpty()) {
            return HttpOptional.empty(maybeRawValue.statusCode());
        }

        String rawValue = maybeRawValue.get();
        byte[] json = serializer.serialize(rawValue);
        return serializer.tryDeserialize(json, paramTypeRef, StatusCodes.BAD_REQUEST);
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

    private RequestParser(HttpServerExchange exchange, JsonSerializer serializer) {
        this.exchange = exchange;
        this.serializer = serializer;
    }

    /** Adapts a {@link RequestBodyCallback} to a {@link Receiver.FullBytesCallback}. */
    private final class AdaptedRequestBodyCallback<B> implements Receiver.FullBytesCallback {

        private final TypeReference<B> bodyTypeRef;
        private final RequestBodyCallback<B> callback;

        public AdaptedRequestBodyCallback(TypeReference<B> bodyTypeRef, RequestBodyCallback<B> callback) {
            this.bodyTypeRef = bodyTypeRef;
            this.callback = callback;
        }

        @Override
        public void handle(HttpServerExchange exchange, byte[] rawBody) {
            HttpOptional<B> maybeBody = serializer.tryDeserialize(rawBody, bodyTypeRef, StatusCodes.BAD_REQUEST);
            if (maybeBody.isEmpty()) {
                sendErrorCode(maybeBody.statusCode());
                return;
            }
            B body = maybeBody.get();

            // FullBytesCallback does not throw an exception, so we must handle exceptions here.
            try {
                callback.handleRequest(exchange, RequestParser.this, body);
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
