package org.example.age.api.infra;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.mikewacker.drift.api.HttpOptional;
import io.github.mikewacker.drift.json.JsonValues;
import io.undertow.io.Receiver;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import java.util.ArrayDeque;
import java.util.Deque;
import org.example.age.api.adapter.Extractor;

/** Repository of {@link Extractor}'s for Undertow that use JSON. */
final class UndertowJsonExtractors {

    /** Creates an {@link Extractor.Async} that reads and deserializes the request body, or sends a 400 error. */
    public static <V> Extractor.Async<HttpServerExchange, V> body(TypeReference<V> valueTypeRef) {
        return new BodyExtractor<>(valueTypeRef);
    }

    /** Creates an {@link Extractor} that gets the (first) value of a query parameter, or returns a 400 error. */
    public static Extractor<HttpServerExchange, String> queryParam(String name) {
        return new QueryParamTextExtractor(name);
    }

    /**
     * Creates an {@link Extractor} that gets and deserializes the (first) value of a query parameter,
     * or returns a 400 error.
     */
    public static <V> Extractor<HttpServerExchange, V> queryParam(String name, TypeReference<V> valueTypeRef) {
        return new QueryParamJsonExtractor<>(name, valueTypeRef);
    }

    // static class
    private UndertowJsonExtractors() {}

    /** {@link Extractor.Async} that reads and deserializes the request body, or sends a 400 error. */
    private record BodyExtractor<V>(TypeReference<V> valueTypeRef) implements Extractor.Async<HttpServerExchange, V> {

        @Override
        public void tryExtract(HttpServerExchange exchange, Extractor.Callback<V> callback) throws Exception {
            Receiver.FullBytesCallback bodyCallback = new BodyCallback<>(callback, valueTypeRef);
            exchange.getRequestReceiver().receiveFullBytes(bodyCallback);
        }
    }

    /** Called when the request body has been read. */
    private record BodyCallback<V>(Extractor.Callback<V> callback, TypeReference<V> valueTypeRef)
            implements Receiver.FullBytesCallback {

        @Override
        public void handle(HttpServerExchange exchange, byte[] rawValue) {
            HttpOptional<V> maybeValue = JsonValues.tryDeserialize(rawValue, valueTypeRef, StatusCodes.BAD_REQUEST);

            // FullBytesCallback does not throw an exception, so we must handle exceptions here.
            try {
                callback.onValueExtracted(maybeValue);
            } catch (Exception e) {
                UndertowResponses.sendStatusCode(exchange, StatusCodes.INTERNAL_SERVER_ERROR);
            }
        }
    }

    /** {@link Extractor} that gets the (first) value of a query parameter, or returns a 400 error. */
    private record QueryParamTextExtractor(String name) implements Extractor<HttpServerExchange, String> {

        private static final Deque<String> EMPTY_VALUES = new ArrayDeque<>();

        @Override
        public HttpOptional<String> tryExtract(HttpServerExchange exchange) {
            Deque<String> values = exchange.getQueryParameters().getOrDefault(name, EMPTY_VALUES);
            if (values.isEmpty()) {
                return HttpOptional.empty(StatusCodes.BAD_REQUEST);
            }
            String value = values.getFirst();

            return HttpOptional.of(value);
        }
    }

    /** {@link Extractor} that gets and deserializes the (first) value of a query parameter, or returns a 400 error. */
    private record QueryParamJsonExtractor<V>(String name, TypeReference<V> valueTypeRef)
            implements Extractor<HttpServerExchange, V> {

        @Override
        public HttpOptional<V> tryExtract(HttpServerExchange exchange) {
            Extractor<HttpServerExchange, String> textExtractor = new QueryParamTextExtractor(name);
            HttpOptional<String> maybeTextValue = textExtractor.tryExtract(exchange);
            if (maybeTextValue.isEmpty()) {
                return maybeTextValue.convertEmpty();
            }
            String textValue = maybeTextValue.get();

            byte[] rawValue = JsonValues.serialize(textValue);
            return JsonValues.tryDeserialize(rawValue, valueTypeRef, StatusCodes.BAD_REQUEST);
        }
    }
}
