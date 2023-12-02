package org.example.age.infra.client;

import java.io.IOException;
import java.util.Map;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * HTTP request for a JSON API.
 *
 * <p>Different consumers may have different requirements for...</p>
 * <ul>
 *     <li>how to create the {@link OkHttpClient}.</li>
 *     <li>how to process the {@link Response}.</li>
 *     <li>how to serialize and deserialize objects to and from JSON, including error handling.</li>
 *     <li>whether to support synchronous calls, asynchronous calls, or both.</li>
 * </ul>
 *
 * <p>In practice, consumers will create a specialized builder that is backed by a {@link JsonApiRequest.Builder}.</p>
 */
public final class JsonApiRequest {

    /** Creates a builder for an API request, using the specified client. */
    public static Builder builder(OkHttpClient client) {
        return new Builder(client);
    }

    // static class
    private JsonApiRequest() {}

    /** Builder for an API request. */
    public static final class Builder {

        private static final MediaType JSON_CONTENT_TYPE = MediaType.get("application/json");
        private static final RequestBody EMPTY_BODY = RequestBody.create(new byte[0]);

        private final OkHttpClient client;
        private final Request.Builder requestBuilder = new Request.Builder();

        /** Sets the URL. */
        public Builder url(String url) {
            requestBuilder.url(url);
            return this;
        }

        /** Adds headers. */
        public Builder headers(Map<String, String> headers) {
            headers.forEach((name, value) -> requestBuilder.header(name, value));
            return this;
        }

        /** Uses a GET request. */
        public Builder get() {
            requestBuilder.get();
            return this;
        }

        /** Uses a POST request without a body. */
        public Builder post() {
            requestBuilder.post(EMPTY_BODY);
            return this;
        }

        /** Uses a POST request with a body. */
        public Builder post(byte[] json) {
            RequestBody body = RequestBody.create(json, JSON_CONTENT_TYPE);
            requestBuilder.post(body);
            return this;
        }

        /** Issues the request synchronously, returning the response. */
        public Response execute() throws IOException {
            Request request = requestBuilder.build();
            return client.newCall(request).execute();
        }

        /** Issues the request asynchronously, using a callback. */
        public void enqueue(Callback callback) {
            Request request = requestBuilder.build();
            client.newCall(request).enqueue(callback);
        }

        private Builder(OkHttpClient client) {
            this.client = client;
        }
    }
}
