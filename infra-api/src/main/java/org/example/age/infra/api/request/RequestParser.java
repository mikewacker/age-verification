package org.example.age.infra.api.request;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.undertow.io.Receiver;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import org.example.age.api.HttpOptional;

/** Parses API arguments from an HTTP request. */
public final class RequestParser {

    private static final Deque<String> EMPTY_VALUES = new ArrayDeque<>();

    private final HttpServerExchange exchange;
    private final ObjectMapper mapper;

    /** Creates a {@link RequestParser} for the {@link HttpServerExchange}. */
    public static RequestParser create(HttpServerExchange exchange, ObjectMapper mapper) {
        return new RequestParser(exchange, mapper);
    }

    /**
     * Asynchronously reads the JSON body.
     *
     * <p>Sends a 400 error if the body cannot be deserialized,
     * or a 500 error if the callback throws an uncaught exception.</p>
     */
    public <B> void parseBody(TypeReference<B> bodyTypeRef, RequestJsonCallback<B> callback) {
        Receiver.FullBytesCallback adaptedCallback = new AdaptedFullBytesCallback<>(bodyTypeRef, callback);
        exchange.getRequestReceiver().receiveFullBytes(adaptedCallback);
    }

    /**
     * Gets the (first) value of a query parameter, if present.
     *
     * <p>Returns a 400 error if the parameter is missing or cannot be deserialized.</p>
     */
    public <P> HttpOptional<P> tryGetQueryParameter(String name, TypeReference<P> bodyTypeRef) {
        HttpOptional<String> maybeRawValue = tryGetQueryParameter(name);
        if (maybeRawValue.isEmpty()) {
            return HttpOptional.empty(maybeRawValue.statusCode());
        }

        String rawValue = maybeRawValue.get();
        try {
            String json = mapper.writeValueAsString(rawValue);
            P value = mapper.readValue(json, bodyTypeRef);
            return HttpOptional.of(value);
        } catch (IOException e) {
            return HttpOptional.empty(StatusCodes.BAD_REQUEST);
        }
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

    private RequestParser(HttpServerExchange exchange, ObjectMapper mapper) {
        this.exchange = exchange;
        this.mapper = mapper;
    }

    /** Adapts a {@link RequestJsonCallback} to a {@link Receiver.FullBytesCallback}. */
    private final class AdaptedFullBytesCallback<B> implements Receiver.FullBytesCallback {

        private final TypeReference<B> bodyTypeRef;
        private final RequestJsonCallback<B> callback;

        public AdaptedFullBytesCallback(TypeReference<B> bodyTypeRef, RequestJsonCallback<B> callback) {
            this.bodyTypeRef = bodyTypeRef;
            this.callback = callback;
        }

        @Override
        public void handle(HttpServerExchange exchange, byte[] rawBody) {
            B body;
            try {
                body = mapper.readValue(rawBody, bodyTypeRef);
            } catch (IOException e) {
                sendError(StatusCodes.BAD_REQUEST);
                return;
            }

            // FullBytesCallback does not throw an exception, so we must handle exceptions here.
            try {
                callback.handleRequest(exchange, RequestParser.this, body);
            } catch (Exception e) {
                sendError(StatusCodes.INTERNAL_SERVER_ERROR);
            }
        }

        /** Sends an error status code. */
        private void sendError(int statusCode) {
            exchange.setStatusCode(statusCode);
            exchange.endExchange();
        }
    }
}
