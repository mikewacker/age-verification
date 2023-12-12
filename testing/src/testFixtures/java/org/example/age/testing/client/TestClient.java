package org.example.age.testing.client;

import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import java.util.Map;
import okhttp3.MediaType;
import okhttp3.Response;
import org.example.age.api.base.HttpOptional;
import org.example.age.client.infra.JsonApiClient;
import org.example.age.data.json.JsonValues;

/** Shared HTTP client for a JSON API. */
public final class TestClient {

    /** Creates a builder for a JSON API request. */
    public static RequestBuilder requestBuilder() {
        return new RequestBuilder();
    }

    // static class
    private TestClient() {}

    /** Builder for a JSON API request. */
    public static final class RequestBuilder {

        private static final MediaType JSON_CONTENT_TYPE = MediaType.get("application/json");

        private final JsonApiClient.RequestBuilder requestBuilder =
                JsonApiClient.requestBuilder(TestOkHttpClient.get());

        /** Uses a GET request at the specified URL. */
        public RequestBuilder get(String url) {
            requestBuilder.get(url);
            return this;
        }

        /** Uses a POST request at the specified URL. */
        public RequestBuilder post(String url) {
            requestBuilder.post(url);
            return this;
        }

        /** Sets the headers. */
        public RequestBuilder headers(Map<String, String> headers) {
            requestBuilder.headers(headers);
            return this;
        }

        /** Sets the body. */
        public RequestBuilder body(Object requestValue) {
            requestBuilder.body(requestValue);
            return this;
        }

        /** Makes the request synchronously, returning a status code. */
        public int executeWithStatusCodeResponse() throws IOException {
            Response response = requestBuilder.execute();
            return response.code();
        }

        /** Makes the request synchronously, returning a response value or an error status code. */
        public <V> HttpOptional<V> executeWithJsonResponse(TypeReference<V> responseValueTypeRef) throws IOException {
            Response response = requestBuilder.execute();
            if (!response.isSuccessful()) {
                return HttpOptional.empty(response.code());
            }

            TestOkHttpClient.assertContentType(response, JSON_CONTENT_TYPE);
            byte[] rawResponseValue = response.body().bytes();
            V responseValue = JsonValues.deserialize(rawResponseValue, responseValueTypeRef);
            return HttpOptional.of(responseValue);
        }

        private RequestBuilder() {}
    }
}
