package org.example.age.service.infra.client;

import com.fasterxml.jackson.core.type.TypeReference;
import org.example.age.api.base.ApiHandler;
import org.example.age.api.base.Dispatcher;
import org.example.age.api.base.HttpOptional;
import org.example.age.api.base.Sender;

/**
 * Dispatches HTTP requests to a backend server as part of a frontend exchange.
 *
 * <p>Failures for the backend request are not handled by the callback; the frontend server will send a 502 error.</p>
 */
public interface RequestDispatcher {

    /** Creates a builder for a request whose response is only a status code. */
    UrlStageRequestBuilder<Integer> requestBuilder(Dispatcher dispatcher);

    /** Creates a builder for a request whose response is a value (or an error status code). */
    <V> UrlStageRequestBuilder<HttpOptional<V>> requestBuilder(
            Dispatcher dispatcher, TypeReference<V> responseValueTypeRef);

    /** Builder for a request that can set the method and the URL together. */
    interface UrlStageRequestBuilder<V> {

        /** Uses a GET request at the specified URL. */
        FinalStageRequestBuilder<V> get(String url);

        /** Uses a POST request at the specified URL. */
        BodyOrFinalStageRequestBuilder<V> post(String url);
    }

    /** Builder for a request that can set the body, or build and dispatch the request. */
    interface BodyOrFinalStageRequestBuilder<V> extends FinalStageRequestBuilder<V> {

        /** Sets the body. */
        FinalStageRequestBuilder<V> body(Object requestValue);
    }

    /** Builder for a request that can build and dispatch the request. */
    interface FinalStageRequestBuilder<V> {

        /** Dispatches the request using a callback. */
        <S extends Sender> void dispatch(S sender, ApiHandler.OneArg<S, V> callback);

        /** Dispatches the request using a callback, passing one additional argument along to the callback. */
        default <S extends Sender, A> void dispatch(S sender, A arg, ApiHandler.TwoArg<S, A, V> callback) {
            dispatch(sender, (s, responseValue, d) -> callback.handleRequest(s, arg, responseValue, d));
        }

        /** Dispatches the request using a callback, passing two additional arguments along to the callback. */
        default <S extends Sender, A1, A2> void dispatch(
                S sender, A1 arg1, A2 arg2, ApiHandler.ThreeArg<S, A1, A2, V> callback) {
            dispatch(sender, (s, responseValue, d) -> callback.handleRequest(s, arg1, arg2, responseValue, d));
        }

        /** Dispatches the request using a callback, passing three additional arguments along to the callback. */
        default <S extends Sender, A1, A2, A3> void dispatch(
                S sender, A1 arg1, A2 arg2, A3 arg3, ApiHandler.FourArg<S, A1, A2, A3, V> callback) {
            dispatch(sender, (s, responseValue, d) -> callback.handleRequest(s, arg1, arg2, arg3, responseValue, d));
        }
    }
}
