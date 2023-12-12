package org.example.age.service.infra.client;

import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.example.age.api.base.ApiHandler;
import org.example.age.api.base.Dispatcher;
import org.example.age.api.base.HttpOptional;
import org.example.age.api.base.Sender;
import org.example.age.client.infra.JsonApiClient;
import org.example.age.client.infra.ResponseConverter;
import org.example.age.data.json.JsonValues;
import org.example.age.service.infra.client.internal.DispatcherOkHttpClient;

@Singleton
final class RequestDispatcherImpl implements RequestDispatcher {

    private final DispatcherOkHttpClient client;

    @Inject
    public RequestDispatcherImpl(DispatcherOkHttpClient client) {
        this.client = client;
    }

    @Override
    public RequestBuilder<Integer> requestBuilder(Dispatcher dispatcher) {
        return new RequestBuilderImpl<>(dispatcher, Response::code);
    }

    @Override
    public <V> RequestBuilder<HttpOptional<V>> requestBuilder(
            Dispatcher dispatcher, TypeReference<V> responseValueTypeRef) {
        ResponseConverter<HttpOptional<V>> responseConverter = new JsonValueResponseConverter<>(responseValueTypeRef);
        return new RequestBuilderImpl<>(dispatcher, responseConverter);
    }

    /** Internal {@link RequestBuilder} implementation. */
    private final class RequestBuilderImpl<V> implements RequestBuilder<V> {

        private final JsonApiClient.RequestBuilder requestBuilder;

        private final Dispatcher dispatcher;
        private final ResponseConverter<V> responseConverter;

        @Override
        public RequestBuilder<V> get(String url) {
            requestBuilder.get(url);
            return this;
        }

        @Override
        public RequestBuilder<V> post(String url) {
            requestBuilder.post(url);
            return this;
        }

        @Override
        public RequestBuilder<V> body(Object requestValue) {
            requestBuilder.body(requestValue);
            return this;
        }

        @Override
        public <S extends Sender> void dispatch(S sender, ApiHandler.OneArg<S, V> callback) {
            Callback adaptedCallback = new AdaptedCallback<>(sender, responseConverter, dispatcher, callback);
            requestBuilder.enqueue(adaptedCallback);
            dispatcher.dispatched();
        }

        private RequestBuilderImpl(Dispatcher dispatcher, ResponseConverter<V> responseConverter) {
            requestBuilder = JsonApiClient.requestBuilder(client.get(dispatcher));
            this.dispatcher = dispatcher;
            this.responseConverter = responseConverter;
        }
    }

    /** Reads the response body and deserializes it from JSON, or returns an error status code. */
    private record JsonValueResponseConverter<V>(TypeReference<V> valueTypeRef)
            implements ResponseConverter<HttpOptional<V>> {

        @Override
        public HttpOptional<V> convert(Response response) throws IOException {
            if (!response.isSuccessful()) {
                return HttpOptional.empty(response.code());
            }

            byte[] rawValue = response.body().bytes();
            Optional<V> maybeValue = JsonValues.tryDeserialize(rawValue, valueTypeRef);
            if (maybeValue.isEmpty()) {
                throw new IOException("deserialization failed");
            }
            V value = maybeValue.get();

            return HttpOptional.of(value);
        }
    }

    /**
     * Adapts an {@link ApiHandler.OneArg} to a {@link Callback}.
     *
     * <p>A lambda function can adapt {@link ApiHandler}'s with more arguments to a {@link ApiHandler.OneArg}.</p>
     */
    private record AdaptedCallback<S extends Sender, V>(
            S sender, ResponseConverter<V> responseConverter, Dispatcher dispatcher, ApiHandler.OneArg<S, V> callback)
            implements Callback {

        @Override
        public void onResponse(Call call, Response response) {
            dispatcher.executeHandler(() -> onResponse(response));
        }

        @Override
        public void onFailure(Call call, IOException e) {
            dispatcher.executeHandler(this::onFailure);
        }

        /** Callback for a response. */
        private void onResponse(Response response) throws Exception {
            V responseValue;
            try {
                responseValue = responseConverter.convert(response);
            } catch (IOException e) {
                onFailure();
                return;
            }

            callback.handleRequest(sender, responseValue, dispatcher);
        }

        /** Callback for a failure. */
        private void onFailure() {
            sender.sendErrorCode(502);
        }
    }
}
