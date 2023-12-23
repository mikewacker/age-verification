package org.example.age.service.infra.client;

import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import java.util.Optional;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.example.age.api.base.ApiHandler;
import org.example.age.api.base.Dispatcher;
import org.example.age.api.base.HttpOptional;
import org.example.age.api.base.Sender;
import org.example.age.client.infra.JsonApiClient;
import org.example.age.client.infra.ResponseConverter;
import org.example.age.data.json.JsonValues;

final class RequestDispatcherImpl implements RequestDispatcher {

    private final DispatcherOkHttpClientProvider clientProvider = DispatcherOkHttpClientProvider.create();

    public static RequestDispatcher create() {
        return new RequestDispatcherImpl();
    }

    @Override
    public UrlStageRequestBuilder<Integer> requestBuilder(Dispatcher dispatcher) {
        OkHttpClient client = clientProvider.get(dispatcher);
        JsonApiClient.UrlStageRequestBuilder requestBuilder = JsonApiClient.requestBuilder(client);
        AdaptedDispatcher<Integer> adaptedDispatcher = new AdaptedDispatcher<>(dispatcher, Response::code);
        return new UrlStageRequestBuilderImpl<>(requestBuilder, adaptedDispatcher);
    }

    @Override
    public <V> UrlStageRequestBuilder<HttpOptional<V>> requestBuilder(
            Dispatcher dispatcher, TypeReference<V> responseValueTypeRef) {
        OkHttpClient client = clientProvider.get(dispatcher);
        JsonApiClient.UrlStageRequestBuilder requestBuilder = JsonApiClient.requestBuilder(client);
        ResponseConverter<HttpOptional<V>> responseConverter = new JsonValueResponseConverter<>(responseValueTypeRef);
        AdaptedDispatcher<HttpOptional<V>> adaptedDispatcher = new AdaptedDispatcher<>(dispatcher, responseConverter);
        return new UrlStageRequestBuilderImpl<>(requestBuilder, adaptedDispatcher);
    }

    private RequestDispatcherImpl() {}

    /** Internal {@link UrlStageRequestBuilder} implementation. */
    private record UrlStageRequestBuilderImpl<V>(
            JsonApiClient.UrlStageRequestBuilder requestBuilder, AdaptedDispatcher<V> dispatcher)
            implements UrlStageRequestBuilder<V> {

        @Override
        public FinalStageRequestBuilder<V> get(String url) {
            return new FinalStageRequestBuilderImpl<>(requestBuilder.get(url), dispatcher);
        }

        @Override
        public BodyOrFinalStageRequestBuilder<V> post(String url) {
            return new BodyOrFinalStageRequestBuilderImpl<>(requestBuilder.post(url), dispatcher);
        }
    }

    /**
     * Internal {@link FinalStageRequestBuilder} implementation.
     *
     * <p>Also implements the {@link UrlStageRequestBuilder#get} branch.</p>
     */
    private record FinalStageRequestBuilderImpl<V>(
            JsonApiClient.FinalStageRequestBuilder requestBuilder, AdaptedDispatcher<V> dispatcher)
            implements FinalStageRequestBuilder<V> {

        @Override
        public <S extends Sender> void dispatch(S sender, ApiHandler.OneArg<S, V> callback) {
            dispatcher.dispatch(requestBuilder, sender, callback);
        }
    }

    /**
     * Internal {@link BodyOrFinalStageRequestBuilder} (and {@link FinalStageRequestBuilder}) implementation.
     *
     * <p>Also implements the {@link UrlStageRequestBuilder#post} branch.</p>
     */
    private record BodyOrFinalStageRequestBuilderImpl<V>(
            JsonApiClient.BodyOrFinalStageRequestBuilder requestBuilder, AdaptedDispatcher<V> dispatcher)
            implements BodyOrFinalStageRequestBuilder<V> {

        @Override
        public FinalStageRequestBuilder<V> body(Object requestValue) {
            requestBuilder.body(requestValue);
            return this;
        }

        @Override
        public <S extends Sender> void dispatch(S sender, ApiHandler.OneArg<S, V> callback) {
            dispatcher.dispatch(requestBuilder, sender, callback);
        }
    }

    /**
     * Adapts {@link FinalStageRequestBuilder#dispatch(Sender, ApiHandler.OneArg)}
     * to {@link JsonApiClient.FinalStageRequestBuilder#enqueue(Callback)}.
     */
    private record AdaptedDispatcher<V>(Dispatcher dispatcher, ResponseConverter<V> responseConverter) {

        public <S extends Sender> void dispatch(
                JsonApiClient.FinalStageRequestBuilder requestBuilder, S sender, ApiHandler.OneArg<S, V> callback) {
            Callback adaptedCallback = new AdaptedCallback<>(sender, responseConverter, dispatcher, callback);
            requestBuilder.enqueue(adaptedCallback);
            dispatcher.dispatched();
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
