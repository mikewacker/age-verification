package org.example.age.testing.client;

import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import org.example.age.api.base.HttpOptional;
import org.example.age.client.infra.ApiClient;

/** Shared HTTP client for a JSON API. */
public interface TestClient extends ApiClient {

    /** Creates a builder for a JSON API request whose response is only a status code. */
    static UrlStageRequestBuilder<ExecuteStage<Integer>> requestBuilder() {
        return TestClientImpl.requestBuilder();
    }

    /** Creates a builder for a JSON API request whose response is a JSON value (or an error status code). */
    static <V> UrlStageRequestBuilder<ExecuteStage<HttpOptional<V>>> requestBuilder(
            TypeReference<V> responseValueTypeRef) {
        return TestClientImpl.requestBuilder(responseValueTypeRef);
    }

    /** Post-build stage that can synchronously execute the request. */
    interface ExecuteStage<V> {

        /** Executes the request, returning the response. */
        V execute() throws IOException;
    }
}
