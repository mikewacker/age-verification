package org.example.age.infra.service.client;

import com.fasterxml.jackson.core.type.TypeReference;
import okhttp3.HttpUrl;
import org.example.age.api.Dispatcher;
import org.example.age.api.Sender;

/**
 * Dispatches an HTTP request to a backend server as part of a frontend exchange.
 *
 * <p>Failures for the backend request are not handled by the callback; they will result in a 502 error.</p>
 */
public interface RequestDispatcher {

    /** Creates a builder for an exchange with the backend server at the specified URL. */
    <S extends Sender> ExchangeBuilder<S> createExchangeBuilder(HttpUrl url, S sender, Dispatcher dispatcher);

    /** Builder for an exchange with the backend server. */
    interface ExchangeBuilder<S extends Sender> {

        /** Uses a GET request. */
        ExchangeBuilder<S> get();

        /** Uses a POST request without a request body. */
        ExchangeBuilder<S> post();

        /** Uses a POST request with a request body. */
        ExchangeBuilder<S> post(Object requestBody);

        /** Dispatches the request, expecting a response without a body. */
        void dispatchWithoutResponseBody(ResponseCallback<S> callback);

        /** Dispatches the request, expecting a response with a body. */
        <B> void dispatchWithResponseBody(TypeReference<B> responseBodyTypeRef, ResponseBodyCallback<S, B> callback);
    }
}
