package org.example.age.testing.client;

import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import java.util.Map;
import okhttp3.MediaType;
import okhttp3.Response;
import org.example.age.api.base.HttpOptional;
import org.example.age.client.infra.JsonApiClient;
import org.example.age.client.infra.ResponseConverter;
import org.example.age.data.json.JsonValues;

final class TestClientImpl implements TestClient {

    /** Creates a builder for a JSON API request whose response is only a status code. */
    public static UrlStageRequestBuilder<Integer> requestBuilder() {
        JsonApiClient.UrlStageRequestBuilder requestBuilder = JsonApiClient.requestBuilder(TestOkHttpClient.get());
        AdaptedExecutor<Integer> executor = new AdaptedExecutor<>(Response::code);
        return new UrlStageRequestBuilderImpl<>(requestBuilder, executor);
    }

    /** Creates a builder for a JSON API request whose response is a value (or an error status code). */
    public static <V> UrlStageRequestBuilder<HttpOptional<V>> requestBuilder(TypeReference<V> responseValueTypeRef) {
        JsonApiClient.UrlStageRequestBuilder requestBuilder = JsonApiClient.requestBuilder(TestOkHttpClient.get());
        ResponseConverter<HttpOptional<V>> responseConverter = new JsonValueResponseConverter<>(responseValueTypeRef);
        AdaptedExecutor<HttpOptional<V>> executor = new AdaptedExecutor<>(responseConverter);
        return new UrlStageRequestBuilderImpl<>(requestBuilder, executor);
    }

    // static class
    private TestClientImpl() {}

    /** Internal {@link UrlStageRequestBuilder} implementation. */
    private record UrlStageRequestBuilderImpl<V>(
            JsonApiClient.UrlStageRequestBuilder requestBuilder, AdaptedExecutor<V> executor)
            implements TestClient.UrlStageRequestBuilder<V> {

        @Override
        public HeadersOrFinalStageRequestBuilder<V> get(String url) {
            return new HeadersOrFinalStageRequestBuilderImpl<>(requestBuilder.get(url), executor);
        }

        @Override
        public HeadersOrBodyOrFinalStageRequestBuilder<V> post(String url) {
            return new HeadersOrBodyOrFinalStageRequestBuilderImpl<>(requestBuilder.post(url), executor);
        }
    }

    /**
     * Internal {@link HeadersOrFinalStageRequestBuilder} (and {@link FinalStageRequestBuilder}) implementation.
     *
     * <p>Also implements the {@link UrlStageRequestBuilder#get} branch.</p>
     */
    private record HeadersOrFinalStageRequestBuilderImpl<V>(
            JsonApiClient.HeadersOrFinalStageRequestBuilder requestBuilder, AdaptedExecutor<V> executor)
            implements HeadersOrFinalStageRequestBuilder<V> {

        @Override
        public FinalStageRequestBuilder<V> headers(Map<String, String> headers) {
            requestBuilder.headers(headers);
            return this;
        }

        @Override
        public V execute() throws IOException {
            return executor.execute(requestBuilder);
        }
    }

    /**
     * Internal {@link HeadersOrBodyOrFinalStageRequestBuilder}
     * (and {@link FinalStageRequestBuilder} and {@link FinalStageRequestBuilder}) implementation.
     *
     * <p>Also implements the {@link UrlStageRequestBuilder#post} branch.</p>
     */
    private record HeadersOrBodyOrFinalStageRequestBuilderImpl<V>(
            JsonApiClient.HeadersOrBodyOrFinalStageRequestBuilder requestBuilder, AdaptedExecutor<V> executor)
            implements HeadersOrBodyOrFinalStageRequestBuilder<V> {

        @Override
        public BodyOrFinalStageRequestBuilder<V> headers(Map<String, String> headers) {
            requestBuilder.headers(headers);
            return this;
        }

        @Override
        public FinalStageRequestBuilder<V> body(Object requestValue) {
            requestBuilder.body(requestValue);
            return this;
        }

        @Override
        public V execute() throws IOException {
            return executor.execute(requestBuilder);
        }
    }

    /**
     * Adapts {@link FinalStageRequestBuilder#execute()} to {@link JsonApiClient.FinalStageRequestBuilder#execute()}.
     */
    private record AdaptedExecutor<V>(ResponseConverter<V> responseConverter) {

        public V execute(JsonApiClient.FinalStageRequestBuilder requestBuilder) throws IOException {
            Response response = requestBuilder.execute();
            return responseConverter.convert(response);
        }
    }

    /** Reads the response body and deserializes it from JSON, or returns an error status code. */
    private record JsonValueResponseConverter<V>(TypeReference<V> valueTypeRef)
            implements ResponseConverter<HttpOptional<V>> {

        private static final MediaType JSON_CONTENT_TYPE = MediaType.get("application/json");

        @Override
        public HttpOptional<V> convert(Response response) throws IOException {
            if (!response.isSuccessful()) {
                return HttpOptional.empty(response.code());
            }

            TestOkHttpClient.assertContentType(response, JSON_CONTENT_TYPE);
            byte[] rawValue = response.body().bytes();
            V value = JsonValues.deserialize(rawValue, valueTypeRef);
            return HttpOptional.of(value);
        }
    }
}
