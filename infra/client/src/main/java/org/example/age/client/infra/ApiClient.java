package org.example.age.client.infra;

import java.util.Map;

/** HTTP client for an API. The request builder includes a post-build stage that can make the request. */
public interface ApiClient {

    /** Builder for an API request that can set the method and the URL together. */
    interface UrlStageRequestBuilder<S> {

        /** Uses a GET request at the specified URL. */
        HeadersOrFinalStageRequestBuilder<S> get(String url);

        /** Uses a POST request at the specified URL. */
        HeadersOrBodyOrFinalStageRequestBuilder<S> post(String url);
    }

    /** Builder for an API request that can set the headers, or build the request. */
    interface HeadersOrFinalStageRequestBuilder<S> extends FinalStageRequestBuilder<S> {

        /** Sets the headers. */
        FinalStageRequestBuilder<S> headers(Map<String, String> headers);
    }

    /** Builder for an API request that can set the headers, set the body, or build the request. */
    interface HeadersOrBodyOrFinalStageRequestBuilder<S> extends BodyOrFinalStageRequestBuilder<S> {

        /** Sets the headers. */
        BodyOrFinalStageRequestBuilder<S> headers(Map<String, String> headers);
    }

    /** Builder for an API request that can set the body, or build the request. */
    interface BodyOrFinalStageRequestBuilder<S> extends FinalStageRequestBuilder<S> {

        /** Sets the body. */
        FinalStageRequestBuilder<S> body(Object requestValue);
    }

    /** Builder for an API request that can build the request. */
    interface FinalStageRequestBuilder<S> {

        /** Builds the request, returning a post-build stage that can make the request. */
        S build();
    }
}
