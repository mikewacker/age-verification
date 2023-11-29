package org.example.age.infra.service.client;

import com.fasterxml.jackson.core.type.TypeReference;
import org.example.age.api.Dispatcher;
import org.example.age.api.Sender;

/**
 * Dispatches an HTTP request to a backend server as part of a frontend exchange.
 *
 * <p>Failures for the backend request are not handled by the callback; they will result in a 502 error.</p>
 */
public interface RequestDispatcher {

    /** Creates a builder for a request to the backend server at the specified URL. */
    <S extends Sender> RequestBuilder<S> requestBuilder(String url, S sender, Dispatcher dispatcher);

    /** Builder for a request to the backend server. */
    interface RequestBuilder<S extends Sender> {

        /** Uses a GET request. */
        RequestBuilder<S> get();

        /** Uses a POST request without a request body. */
        RequestBuilder<S> post();

        /** Uses a POST request with a request body. */
        RequestBuilder<S> post(Object requestBody);

        /** Builds and dispatches the request, expecting a response with only a status code. */
        void dispatchWithStatusCodeResponse(ResponseStatusCodeCallback<S> callback);

        /** Builds and dispatches the request, expecting a response with a JSON body. */
        <B> void dispatchWithJsonResponse(TypeReference<B> responseBodyTypeRef, ResponseJsonCallback<S, B> callback);
    }
}
