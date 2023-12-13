package org.example.age.testing.client;

import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import java.util.Map;
import org.example.age.api.base.HttpOptional;

/** Shared HTTP client for a JSON API. */
public interface TestClient {

    /** Creates a builder for a JSON API request whose response is only a status code. */
    static UrlStageRequestBuilder<Integer> requestBuilder() {
        return TestClientImpl.requestBuilder();
    }

    /** Creates a builder for a JSON API request whose response is a value (or an error status code). */
    static <V> UrlStageRequestBuilder<HttpOptional<V>> requestBuilder(TypeReference<V> responseValueTypeRef) {
        return TestClientImpl.requestBuilder(responseValueTypeRef);
    }

    /** Builder for a JSON API request that can set the method and the URL together. */
    interface UrlStageRequestBuilder<V> {

        /** Uses a GET request at the specified URL. */
        HeadersOrFinalStageRequestBuilder<V> get(String url);

        /** Uses a POST request at the specified URL. */
        HeadersOrBodyOrFinalStageRequestBuilder<V> post(String url);
    }

    /** Builder for a JSON API request that can set the headers, or build and make the request. */
    interface HeadersOrFinalStageRequestBuilder<V> extends FinalStageRequestBuilder<V> {

        /** Sets the headers. */
        FinalStageRequestBuilder<V> headers(Map<String, String> headers);
    }

    /** Builder for a JSON API request that can set the headers, set the body, or build and make the request. */
    interface HeadersOrBodyOrFinalStageRequestBuilder<V> extends BodyOrFinalStageRequestBuilder<V> {

        /** Sets the headers. */
        BodyOrFinalStageRequestBuilder<V> headers(Map<String, String> headers);
    }

    /** Builder for a JSON API request that can set the body, or build and make the request. */
    interface BodyOrFinalStageRequestBuilder<V> extends FinalStageRequestBuilder<V> {

        /** Sets the body. */
        FinalStageRequestBuilder<V> body(Object requestValue);
    }

    /** Builder for a JSON API request that can build and make the request. */
    interface FinalStageRequestBuilder<V> {

        /** Makes the request synchronously, returning the response. */
        V execute() throws IOException;
    }
}
