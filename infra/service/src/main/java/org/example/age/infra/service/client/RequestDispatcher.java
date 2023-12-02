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

    /** Creates a builder for a request to the backend server. */
    <S extends Sender> RequestBuilder<S> requestBuilder(S sender, Dispatcher dispatcher);

    /** Builder for a request to the backend server. */
    interface RequestBuilder<S extends Sender> {

        /** Sets the URL. */
        RequestBuilder<S> url(String url);

        /** Uses a GET request. */
        RequestBuilder<S> get();

        /** Uses a POST request without a request body. */
        RequestBuilder<S> post();

        /** Uses a POST request with a request body. */
        RequestBuilder<S> post(Object requestValue);

        /** Dispatches the request, expecting a response with only a status code. */
        void dispatchWithStatusCodeResponse(ResponseStatusCodeCallback<S> callback);

        /** Dispatches the request, expecting a response with a JSON body. */
        <V> void dispatchWithJsonResponse(TypeReference<V> responseValueTypeRef, ResponseJsonCallback<S, V> callback);
    }
}
