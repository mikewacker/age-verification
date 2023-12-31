package org.example.age.client.infra;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.mikewacker.drift.api.HttpOptional;
import java.io.IOException;

/** Shared HTTP client that can synchronously make requests to a JSON API. */
public interface JsonApiClient extends ApiClient {

    /** Creates a builder for a JSON API request whose response is only a status code. */
    static UrlStageRequestBuilder<ExecuteStage<Integer>> requestBuilder() {
        return JsonApiClientImpl.requestBuilder();
    }

    /** Creates a builder for a JSON API request whose response is a JSON value (or an error status code). */
    static <V> UrlStageRequestBuilder<ExecuteStage<HttpOptional<V>>> requestBuilder(
            TypeReference<V> responseValueTypeRef) {
        return JsonApiClientImpl.requestBuilder(responseValueTypeRef);
    }

    /** Post-build stage that can synchronously execute the request. */
    interface ExecuteStage<V> {

        /** Executes the request, returning the response. */
        V execute() throws IOException;
    }
}
