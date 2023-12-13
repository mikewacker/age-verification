package org.example.age.client.infra;

import java.io.IOException;
import java.util.Map;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;

/**
 * HTTP client for a JSON API.
 *
 * <p>Different consumers may have different requirements for...</p>
 * <ul>
 *     <li>how to create the {@link OkHttpClient}.</li>
 *     <li>how to process the {@link Response}.</li>
 *     <li>whether to support synchronous calls, asynchronous calls, or both.</li>
 * </ul>
 *
 * <p>In practice, consumers will create a specialized builder that is backed by a {@link JsonApiClient}.
 * This specialized builder will typically use the {@code OkHttp} library in the implementation,
 * but not expose it as part of the public API.</p>
 */
public interface JsonApiClient {

    /** Creates a builder for a JSON API request, using the specified {@link OkHttpClient}. */
    static UrlStageRequestBuilder requestBuilder(OkHttpClient client) {
        return JsonApiClientImpl.requestBuilder(client);
    }

    /** Builder for a JSON API request that can set the method and the URL together. */
    interface UrlStageRequestBuilder {

        /** Uses a GET request at the specified URL. */
        HeadersOrFinalStageRequestBuilder get(String url);

        /** Uses a POST request at the specified URL. */
        HeadersOrBodyOrFinalStageRequestBuilder post(String url);
    }

    /** Builder for a JSON API request that can set the headers, or build and make the request. */
    interface HeadersOrFinalStageRequestBuilder extends FinalStageRequestBuilder {

        /** Sets the headers. */
        FinalStageRequestBuilder headers(Map<String, String> headers);
    }

    /** Builder for a JSON API request that can set the headers, set the body, or build and make the request. */
    interface HeadersOrBodyOrFinalStageRequestBuilder extends BodyOrFinalStageRequestBuilder {

        /** Sets the headers. */
        BodyOrFinalStageRequestBuilder headers(Map<String, String> headers);
    }

    /** Builder for a JSON API request that can set the body, or build and make the request. */
    interface BodyOrFinalStageRequestBuilder extends FinalStageRequestBuilder {

        /** Sets the body. */
        FinalStageRequestBuilder body(Object requestValue);
    }

    /** Builder for a JSON API request that can build and make the request. */
    interface FinalStageRequestBuilder {

        /** Makes the request synchronously, returning the {@link Response}. */
        Response execute() throws IOException;

        /** Makes the request asynchronously using a {@link Callback}. */
        void enqueue(Callback callback);
    }
}
