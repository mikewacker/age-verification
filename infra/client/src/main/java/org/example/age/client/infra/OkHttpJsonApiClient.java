package org.example.age.client.infra;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.mikewacker.drift.api.HttpOptional;
import io.github.mikewacker.drift.json.JsonValues;
import java.io.IOException;
import java.util.Map;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * JSON {@link ApiClient} that is internally backed by an {@link OkHttpClient}.
 *
 * <p>Implementations will bind the type parameter {@code S}
 * and provide {@code requestBuilder()} and <code>requestBuilder({@link TypeReference}&lt;V&gt;)</code> methods.</p>
 */
public abstract class OkHttpJsonApiClient implements ApiClient {

    // If a generic method has multiple type parameters (e.g., S and V),
    // an @Override of that method cannot bind some type parameters (e.g., bind S but not V).
    // This limitation of Java influences the design of this class.
    // (Also, S will depend on V in implementations. As a result, S also cannot be defined in the class declaration.)

    /** Creates a builder for a JSON API request whose response is only a status code. */
    protected final <S> UrlStageRequestBuilder<S> requestBuilder(BuiltStageFactory<S, Integer> builtStageFactory) {
        return new RequestBuilder<>(builtStageFactory, Response::code);
    }

    /** Creates a builder for a JSON API request whose response is a JSON value (or an error status code). */
    protected final <S, V> UrlStageRequestBuilder<S> requestBuilder(
            BuiltStageFactory<S, HttpOptional<V>> builtStageFactory, TypeReference<V> responseValueTypeRef) {
        ResponseConverter<HttpOptional<V>> responseConverter = new JsonValueResponseConverter<>(responseValueTypeRef);
        return new RequestBuilder<>(builtStageFactory, responseConverter);
    }

    /** Creates the built stage from the built {@link Request} and the {@link ResponseConverter}. */
    @FunctionalInterface
    protected interface BuiltStageFactory<S, V> {

        S create(Request request, ResponseConverter<V> responseConverter);
    }

    /** Converts the {@link Response} to the value. */
    @FunctionalInterface
    protected interface ResponseConverter<V> {

        V convert(Response response) throws IOException;
    }

    /** Builder for a JSON API request. */
    private static final class RequestBuilder<S, V>
            implements UrlStageRequestBuilder<S>,
                    HeadersOrFinalStageRequestBuilder<S>,
                    HeadersOrBodyOrFinalStageRequestBuilder<S> {

        private static final MediaType JSON_CONTENT_TYPE = MediaType.get("application/json");
        private static final RequestBody EMPTY_BODY = RequestBody.create(new byte[0]);

        private final BuiltStageFactory<S, V> builtStageFactory;
        private final ResponseConverter<V> responseConverter;

        private RequestBuilderStage stage = RequestBuilderStage.URL;
        private String method = null;
        private String url = null;
        private Map<String, String> headers = Map.of();
        private RequestBody body = EMPTY_BODY;

        @Override
        public RequestBuilder<S, V> get(String url) {
            checkAndAdvanceStage(RequestBuilderStage.URL, RequestBuilderStage.HEADERS);
            method = "GET";
            this.url = url;
            return this;
        }

        @Override
        public RequestBuilder<S, V> post(String url) {
            checkAndAdvanceStage(RequestBuilderStage.URL, RequestBuilderStage.HEADERS);
            method = "POST";
            this.url = url;
            return this;
        }

        @Override
        public RequestBuilder<S, V> headers(Map<String, String> headers) {
            checkAndAdvanceStage(RequestBuilderStage.HEADERS, RequestBuilderStage.BODY);
            this.headers = Map.copyOf(headers);
            return this;
        }

        @Override
        public RequestBuilder<S, V> body(Object requestValue) {
            checkAndAdvanceStage(RequestBuilderStage.BODY, RequestBuilderStage.FINAL);
            byte[] rawRequestValue = JsonValues.serialize(requestValue);
            body = RequestBody.create(rawRequestValue, JSON_CONTENT_TYPE);
            return this;
        }

        @Override
        public S build() {
            checkAndAdvanceStage(RequestBuilderStage.FINAL, RequestBuilderStage.BUILT);
            Request request = buildRequest();
            return builtStageFactory.create(request, responseConverter);
        }

        /** Builds the {@link Request}. */
        private Request buildRequest() {
            Request.Builder requestBuilder = new Request.Builder().url(url);
            headers.forEach((name, value) -> requestBuilder.header(name, value));
            if (method.equals("GET")) {
                requestBuilder.get();
            } else {
                requestBuilder.method(method, body);
            }
            return requestBuilder.build();
        }

        /** Checks the current builder stage, and also advances the builder stage. */
        private void checkAndAdvanceStage(RequestBuilderStage expected, RequestBuilderStage next) {
            if (stage.compareTo(expected) > 0) {
                throw new IllegalStateException("stage already completed");
            }

            stage = next;
        }

        private RequestBuilder(BuiltStageFactory<S, V> builtStageFactory, ResponseConverter<V> responseConverter) {
            this.builtStageFactory = builtStageFactory;
            this.responseConverter = responseConverter;
        }
    }

    /** Stages for the {@link RequestBuilder}. */
    private enum RequestBuilderStage {
        URL,
        HEADERS,
        BODY,
        FINAL,
        BUILT,
    }

    /**
     * Reads the response body and deserializes it from JSON, or returns an error status code.
     *
     * <p>An exception will be thrown if the response is invalid.</p>
     */
    private record JsonValueResponseConverter<V>(TypeReference<V> responseValueTypeRef)
            implements ResponseConverter<HttpOptional<V>> {

        @Override
        public HttpOptional<V> convert(Response response) throws IOException {
            if (!response.isSuccessful()) {
                return HttpOptional.empty(response.code());
            }

            checkContentType(response);
            byte[] rawResponseValue = response.body().bytes();
            V value = JsonValues.deserialize(rawResponseValue, responseValueTypeRef);
            return HttpOptional.of(value);
        }

        /** Checks that the {@code Content-Type} is {@code application/json}. */
        private void checkContentType(Response response) {
            String rawContentType = response.header("Content-Type");
            if (rawContentType == null) {
                throw new IllegalArgumentException("response Content-Type is missing");
            }

            MediaType contentType = MediaType.parse(rawContentType);
            if (contentType == null) {
                String message = String.format("failed to parse response Content-Type: %s", rawContentType);
                throw new IllegalArgumentException(message);
            }

            if (!contentType.type().equals("application")
                    || !contentType.subtype().equals("json")) {
                String message = String.format("response Content-Type is not application/json: %s", contentType);
                throw new IllegalArgumentException(message);
            }
        }
    }
}
