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

/** Shared HTTP client for a JSON API. */
public final class TestClient {

    /** Creates a builder for a JSON API request whose response is only a status code. */
    public static RequestBuilder<Integer> requestBuilder() {
        return new RequestBuilder<>(Response::code);
    }

    /** Creates a builder for a JSON API request whose response is a value (or an error status code). */
    public static <V> RequestBuilder<HttpOptional<V>> requestBuilder(TypeReference<V> responseValueTypeRef) {
        ResponseConverter<HttpOptional<V>> responseConverter = new JsonValueResponseConverter<>(responseValueTypeRef);
        return new RequestBuilder<>(responseConverter);
    }

    // static class
    private TestClient() {}

    /** Builder for a JSON API request. */
    public static final class RequestBuilder<V> {

        private final JsonApiClient.RequestBuilder requestBuilder =
                JsonApiClient.requestBuilder(TestOkHttpClient.get());

        private final ResponseConverter<V> responseConverter;

        /** Uses a GET request at the specified URL. */
        public RequestBuilder<V> get(String url) {
            requestBuilder.get(url);
            return this;
        }

        /** Uses a POST request at the specified URL. */
        public RequestBuilder<V> post(String url) {
            requestBuilder.post(url);
            return this;
        }

        /** Sets the headers. */
        public RequestBuilder<V> headers(Map<String, String> headers) {
            requestBuilder.headers(headers);
            return this;
        }

        /** Sets the body. */
        public RequestBuilder<V> body(Object requestValue) {
            requestBuilder.body(requestValue);
            return this;
        }

        /** Makes the request synchronously, returning the response. */
        public V execute() throws IOException {
            Response response = requestBuilder.execute();
            return responseConverter.convert(response);
        }

        private RequestBuilder(ResponseConverter<V> responseConverter) {
            this.responseConverter = responseConverter;
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
