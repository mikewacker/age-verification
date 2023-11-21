package org.example.age.infra.service.client;

import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import javax.inject.Inject;
import javax.inject.Singleton;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.example.age.api.Dispatcher;
import org.example.age.api.HttpOptional;
import org.example.age.api.JsonSerializer;
import org.example.age.api.Sender;
import org.example.age.infra.service.client.internal.ExchangeClient;

@Singleton
final class RequestDispatcherImpl implements RequestDispatcher {

    private final ExchangeClient client;
    private final JsonSerializer serializer;

    @Inject
    public RequestDispatcherImpl(ExchangeClient client, JsonSerializer serializer) {
        this.client = client;
        this.serializer = serializer;
    }

    @Override
    public <S extends Sender> ExchangeBuilder<S> createExchangeBuilder(HttpUrl url, S sender, Dispatcher dispatcher) {
        return new ExchangeBuilderImpl<>(url, sender, dispatcher);
    }

    /** {@link ExchangeBuilder} that builds the underlying {@link Request}. */
    private final class ExchangeBuilderImpl<S extends Sender> implements ExchangeBuilder<S> {

        private static final RequestBody EMPTY_BODY = RequestBody.create(new byte[0]);

        private final Request.Builder requestBuilder;
        private final S sender;
        private final Dispatcher dispatcher;

        public ExchangeBuilderImpl(HttpUrl url, S sender, Dispatcher dispatcher) {
            requestBuilder = new Request.Builder().url(url);
            this.sender = sender;
            this.dispatcher = dispatcher;
        }

        @Override
        public ExchangeBuilder<S> get() {
            requestBuilder.get();
            return this;
        }

        @Override
        public ExchangeBuilder<S> post() {
            requestBuilder.post(EMPTY_BODY);
            return this;
        }

        @Override
        public ExchangeBuilder<S> post(Object requestBody) {
            byte[] rawRequestBody = serializer.serialize(requestBody);
            requestBuilder.post(RequestBody.create(rawRequestBody));
            return this;
        }

        @Override
        public void dispatchWithoutResponseBody(ResponseCallback<S> callback) {
            Callback adaptedCallback = new AdaptedResponseCallback<>(sender, dispatcher, callback);
            dispatch(adaptedCallback);
        }

        @Override
        public <B> void dispatchWithResponseBody(
                TypeReference<B> responseBodyTypeRef, ResponseBodyCallback<S, B> callback) {
            Callback adaptedCallback =
                    new AdaptedResponseBodyCallback<>(sender, dispatcher, responseBodyTypeRef, callback);
            dispatch(adaptedCallback);
        }

        /** Dispatches the request using a callback. */
        private void dispatch(Callback callback) {
            Request request = requestBuilder.build();
            Call call = client.getInstance(dispatcher).newCall(request);
            call.enqueue(callback);
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

    /** Adapts a {@link ResponseCallback} to a {@link Callback}. */
    private static final class AdaptedResponseCallback<S extends Sender> extends AdaptedCallback<S> {

        private final ResponseCallback<S> callback;

        public AdaptedResponseCallback(S sender, Dispatcher dispatcher, ResponseCallback<S> callback) {
            super(sender, dispatcher);
            this.callback = callback;
        }

        @Override
        protected void handleResponse(S sender, Response response, Dispatcher dispatcher) throws Exception {
            callback.onResponse(sender, response, dispatcher);
        }
    }

    /** Adapts a {@link ResponseBodyCallback} to a {@link Callback}. */
    private final class AdaptedResponseBodyCallback<S extends Sender, B> extends AdaptedCallback<S> {

        private final TypeReference<B> responseBodyTypeRef;
        private final ResponseBodyCallback<S, B> callback;

        public AdaptedResponseBodyCallback(
                S sender,
                Dispatcher dispatcher,
                TypeReference<B> responseBodyTypeRef,
                ResponseBodyCallback<S, B> callback) {
            super(sender, dispatcher);
            this.responseBodyTypeRef = responseBodyTypeRef;
            this.callback = callback;
        }

        @Override
        protected void handleResponse(S sender, Response response, Dispatcher dispatcher) throws Exception {
            if (!response.isSuccessful()) {
                callback.onResponse(sender, response, null, dispatcher);
                return;
            }

            HttpOptional<byte[]> maybeRawResponseBody = tryReadResponseBody(response);
            if (maybeRawResponseBody.isEmpty()) {
                sender.sendErrorCode(maybeRawResponseBody.statusCode());
                return;
            }
            byte[] rawResponseBody = maybeRawResponseBody.get();

            HttpOptional<B> maybeResponseBody = serializer.tryDeserialize(rawResponseBody, responseBodyTypeRef, 502);
            if (maybeResponseBody.isEmpty()) {
                sender.sendErrorCode(maybeResponseBody.statusCode());
                return;
            }
            B responseBody = maybeResponseBody.get();

            callback.onResponse(sender, response, responseBody, dispatcher);
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
