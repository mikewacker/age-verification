package org.example.age.infra.service.client;

import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import javax.inject.Inject;
import javax.inject.Singleton;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.example.age.api.Dispatcher;
import org.example.age.api.HttpOptional;
import org.example.age.api.JsonSerializer;
import org.example.age.api.Sender;
import org.example.age.infra.client.JsonApiRequest;
import org.example.age.infra.service.client.internal.ExchangeClient;

@Singleton
final class RequestDispatcherImpl implements RequestDispatcher {

    private final ExchangeClient client;

    @Inject
    public RequestDispatcherImpl(ExchangeClient client) {
        this.client = client;
    }

    @Override
    public <S extends Sender> RequestBuilder<S> requestBuilder(S sender, Dispatcher dispatcher) {
        return new RequestBuilderImpl<>(sender, dispatcher);
    }

    /** internal {@link RequestBuilder} implementation. */
    private final class RequestBuilderImpl<S extends Sender> implements RequestBuilder<S> {

        private final JsonApiRequest.Builder requestBuilder;
        private final S sender;
        private final Dispatcher dispatcher;

        private RequestBuilderImpl(S sender, Dispatcher dispatcher) {
            requestBuilder = JsonApiRequest.builder(client.getInstance(dispatcher));
            this.sender = sender;
            this.dispatcher = dispatcher;
        }

        @Override
        public RequestBuilder<S> get(String url) {
            requestBuilder.get(url);
            return this;
        }

        @Override
        public RequestBuilder<S> post(String url) {
            requestBuilder.post(url);
            return this;
        }

        @Override
        public RequestBuilder<S> body(Object requestValue) {
            requestBuilder.body(requestValue);
            return this;
        }

        @Override
        public void dispatchWithStatusCodeResponse(ResponseStatusCodeCallback<S> callback) {
            Callback adaptedCallback = new AdaptedResponseStatusCodeCallback<>(sender, dispatcher, callback);
            dispatch(adaptedCallback);
        }

        @Override
        public <V> void dispatchWithJsonResponse(
                TypeReference<V> responseValueTypeRef, ResponseJsonCallback<S, V> callback) {
            Callback adaptedCallback =
                    new AdaptedResponseJsonCallback<>(sender, dispatcher, responseValueTypeRef, callback);
            dispatch(adaptedCallback);
        }

        /** Dispatches the request using a callback. */
        private void dispatch(Callback callback) {
            requestBuilder.enqueue(callback);
            dispatcher.dispatched();
        }
    }

    /** Adapts a different type of callback to a {@link Callback}. */
    private abstract static class AdaptedCallback<S extends Sender> implements Callback {

        private final S sender;
        private final Dispatcher dispatcher;

        @Override
        public final void onResponse(Call call, Response response) {
            dispatcher.executeHandler(sender, (s, d) -> handleResponse(s, response, d));
        }

        @Override
        public final void onFailure(Call call, IOException e) {
            sender.sendErrorCode(502);
        }

        protected AdaptedCallback(S sender, Dispatcher dispatcher) {
            this.sender = sender;
            this.dispatcher = dispatcher;
        }

        /** Handles a {@link Response} that was received. */
        protected abstract void handleResponse(S sender, Response response, Dispatcher dispatcher) throws Exception;
    }

    /** Adapts a {@link ResponseStatusCodeCallback} to a {@link Callback}. */
    private static final class AdaptedResponseStatusCodeCallback<S extends Sender> extends AdaptedCallback<S> {

        private final ResponseStatusCodeCallback<S> callback;

        public AdaptedResponseStatusCodeCallback(
                S sender, Dispatcher dispatcher, ResponseStatusCodeCallback<S> callback) {
            super(sender, dispatcher);
            this.callback = callback;
        }

        @Override
        protected void handleResponse(S sender, Response response, Dispatcher dispatcher) throws Exception {
            callback.onResponse(sender, response.code(), dispatcher);
        }
    }

    /** Adapts a {@link ResponseJsonCallback} to a {@link Callback}. */
    private static final class AdaptedResponseJsonCallback<S extends Sender, V> extends AdaptedCallback<S> {

        private final TypeReference<V> responseValueTypeRef;
        private final ResponseJsonCallback<S, V> callback;

        public AdaptedResponseJsonCallback(
                S sender,
                Dispatcher dispatcher,
                TypeReference<V> responseBodyTypeRef,
                ResponseJsonCallback<S, V> callback) {
            super(sender, dispatcher);
            this.responseValueTypeRef = responseBodyTypeRef;
            this.callback = callback;
        }

        @Override
        protected void handleResponse(S sender, Response response, Dispatcher dispatcher) throws Exception {
            if (!response.isSuccessful()) {
                callback.onResponse(sender, HttpOptional.empty(response.code()), dispatcher);
                return;
            }

            HttpOptional<byte[]> maybeRawResponseValue = tryReadResponseBody(response);
            if (maybeRawResponseValue.isEmpty()) {
                sender.sendErrorCode(maybeRawResponseValue.statusCode());
                return;
            }
            byte[] rawResponseValue = maybeRawResponseValue.get();

            HttpOptional<V> maybeResponseValue =
                    JsonSerializer.tryDeserialize(rawResponseValue, responseValueTypeRef, 502);
            if (maybeResponseValue.isEmpty()) {
                sender.sendErrorCode(maybeResponseValue.statusCode());
                return;
            }
            V responseValue = maybeResponseValue.get();

            callback.onResponse(sender, HttpOptional.of(responseValue), dispatcher);
        }

        /** Reads the raw response body, or returns a 502 error. */
        private static HttpOptional<byte[]> tryReadResponseBody(Response response) {
            try {
                byte[] rawResponseBody = response.body().bytes();
                return HttpOptional.of(rawResponseBody);
            } catch (IOException e) {
                return HttpOptional.empty(502);
            }
        }
    }
}
