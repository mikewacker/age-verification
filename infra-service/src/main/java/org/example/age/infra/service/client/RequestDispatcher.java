package org.example.age.infra.service.client;

import com.fasterxml.jackson.core.type.TypeReference;
import okhttp3.Request;
import org.example.age.api.Dispatcher;
import org.example.age.api.Sender;

/**
 * Dispatches an HTTP request to a backend server as part of a frontend exchange.
 *
 * <p>Failures for the backend request are not handled by the callback; they will result in a 502 error.</p>
 */
public interface RequestDispatcher {

    /** Dispatches a request whose response does not have a body. */
    <S extends Sender> void dispatch(Request request, S sender, Dispatcher dispatcher, ResponseCallback<S> callback);

    /** Dispatches a request whose response has a body. */
    <B, S extends Sender> void dispatch(
            Request request,
            TypeReference<B> responseBodyTypeRef,
            S sender,
            Dispatcher dispatcher,
            ResponseBodyCallback<B, S> callback);
}
