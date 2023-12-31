package org.example.age.service.infra.client;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.mikewacker.drift.api.ApiHandler;
import io.github.mikewacker.drift.api.Dispatcher;
import io.github.mikewacker.drift.api.HttpOptional;
import io.github.mikewacker.drift.api.Sender;
import org.example.age.client.infra.ApiClient;

/**
 * Dispatches HTTP requests to a backend server as part of a frontend exchange.
 *
 * <p>Failures for the backend request are not handled by the callback; the frontend server will send a 502 error.</p>
 *
 * <p>Consumers should create and share a single {@link RequestDispatcher}; each instance creates a new client.</p>
 */
public interface RequestDispatcher extends ApiClient {

    /** Creates a {@link RequestDispatcher}. */
    static RequestDispatcher create() {
        return RequestDispatcherImpl.create();
    }

    /** Creates a builder for a JSON API request whose response is only a status code. */
    UrlStageRequestBuilder<DispatchStage<Integer>> requestBuilder();

    /** Creates a builder for a JSON API request whose response is a JSON value (or an error status code). */
    <V> UrlStageRequestBuilder<DispatchStage<HttpOptional<V>>> requestBuilder(TypeReference<V> responseValueTypeRef);

    /** Post-build stage that can asynchronously dispatch the request. */
    interface DispatchStage<V> {

        /** Dispatches the request using a callback. */
        <S extends Sender> void dispatch(S sender, Dispatcher dispatcher, ApiHandler.OneArg<S, V> callback);

        /** Dispatches the request using a callback, passing one additional argument along to the callback. */
        default <S extends Sender, A> void dispatch(
                S sender, A arg, Dispatcher dispatcher, ApiHandler.TwoArg<S, A, V> callback) {
            dispatch(sender, dispatcher, (s, responseValue, d) -> callback.handleRequest(s, arg, responseValue, d));
        }

        /** Dispatches the request using a callback, passing two additional arguments along to the callback. */
        default <S extends Sender, A1, A2> void dispatch(
                S sender, A1 arg1, A2 arg2, Dispatcher dispatcher, ApiHandler.ThreeArg<S, A1, A2, V> callback) {
            dispatch(
                    sender,
                    dispatcher,
                    (s, responseValue, d) -> callback.handleRequest(s, arg1, arg2, responseValue, d));
        }

        /** Dispatches the request using a callback, passing three additional arguments along to the callback. */
        default <S extends Sender, A1, A2, A3> void dispatch(
                S sender,
                A1 arg1,
                A2 arg2,
                A3 arg3,
                Dispatcher dispatcher,
                ApiHandler.FourArg<S, A1, A2, A3, V> callback) {
            dispatch(
                    sender,
                    dispatcher,
                    (s, responseValue, d) -> callback.handleRequest(s, arg1, arg2, arg3, responseValue, d));
        }
    }
}
