package org.example.age.service.infra.client;

import com.fasterxml.jackson.core.type.TypeReference;
import org.example.age.api.base.Dispatcher;
import org.example.age.api.base.Sender;

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

        /** Uses a GET request at the specified URL. */
        RequestBuilder<S> get(String url);

        /** Uses a POST request at the specified URL. */
        RequestBuilder<S> post(String url);

        /** Sets the body. */
        RequestBuilder<S> body(Object requestValue);

        /** Dispatches the request, expecting a response with only a status code. */
        void dispatchWithStatusCodeResponse(ResponseStatusCodeCallback<S> callback);

        /** Dispatches the request, expecting a response with a JSON body. */
        <V> void dispatchWithJsonResponse(TypeReference<V> responseValueTypeRef, ResponseJsonCallback<S, V> callback);
    }
}
