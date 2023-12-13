package org.example.age.client.infra;

import java.io.IOException;
import java.util.Map;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.example.age.data.json.JsonValues;

final class JsonApiClientImpl implements JsonApiClient {

    /** Creates a builder for a JSON API request, using the specified {@link OkHttpClient}. */
    public static UrlStageRequestBuilder requestBuilder(OkHttpClient client) {
        return new RequestBuilder(client);
    }

    // static class
    private JsonApiClientImpl() {}

    /** Builder for a JSON API request. */
    private static final class RequestBuilder
            implements UrlStageRequestBuilder,
                    HeadersOrFinalStageRequestBuilder,
                    HeadersOrBodyOrFinalStageRequestBuilder {

        private static final MediaType JSON_CONTENT_TYPE = MediaType.get("application/json");
        private static final RequestBody EMPTY_BODY = RequestBody.create(new byte[0]);

        private final OkHttpClient client;

        private RequestBuilderStage stage = RequestBuilderStage.URL;
        private String method = null;
        private String url = null;
        private Map<String, String> headers = Map.of();
        private RequestBody body = EMPTY_BODY;

        @Override
        public RequestBuilder get(String url) {
            checkAndAdvanceStage(RequestBuilderStage.URL, RequestBuilderStage.HEADERS);
            method = "GET";
            this.url = url;
            return this;
        }

        @Override
        public RequestBuilder post(String url) {
            checkAndAdvanceStage(RequestBuilderStage.URL, RequestBuilderStage.HEADERS);
            method = "POST";
            this.url = url;
            return this;
        }

        @Override
        public RequestBuilder headers(Map<String, String> headers) {
            checkAndAdvanceStage(RequestBuilderStage.HEADERS, RequestBuilderStage.BODY);
            this.headers = Map.copyOf(headers);
            return this;
        }

        @Override
        public RequestBuilder body(Object requestValue) {
            checkAndAdvanceStage(RequestBuilderStage.BODY, RequestBuilderStage.FINAL);
            byte[] rawRequestValue = JsonValues.serialize(requestValue);
            body = RequestBody.create(rawRequestValue, JSON_CONTENT_TYPE);
            return this;
        }

        @Override
        public Response execute() throws IOException {
            checkAndAdvanceStage(RequestBuilderStage.FINAL, RequestBuilderStage.BUILT);
            Request request = build();
            return client.newCall(request).execute();
        }

        @Override
        public void enqueue(Callback callback) {
            checkAndAdvanceStage(RequestBuilderStage.FINAL, RequestBuilderStage.BUILT);
            Request request = build();
            client.newCall(request).enqueue(callback);
        }

        /** Builds the {@link Request}. */
        private Request build() {
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

        private RequestBuilder(OkHttpClient client) {
            this.client = client;
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
}
