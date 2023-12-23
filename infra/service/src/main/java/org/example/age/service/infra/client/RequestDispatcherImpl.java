package org.example.age.service.infra.client;

import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.example.age.api.base.ApiHandler;
import org.example.age.api.base.Dispatcher;
import org.example.age.api.base.HttpOptional;
import org.example.age.api.base.Sender;
import org.example.age.client.infra.OkHttpJsonApiClient;

final class RequestDispatcherImpl extends OkHttpJsonApiClient implements RequestDispatcher {

    private final DispatcherOkHttpClientProvider clientProvider = DispatcherOkHttpClientProvider.create();

    public static RequestDispatcher create() {
        return new RequestDispatcherImpl();
    }

    @Override
    public UrlStageRequestBuilder<DispatchStage<Integer>> requestBuilder() {
        return requestBuilder(DispatchStageImpl::new);
    }

    @Override
    public <V> UrlStageRequestBuilder<DispatchStage<HttpOptional<V>>> requestBuilder(
            TypeReference<V> responseValueTypeRef) {
        return requestBuilder(DispatchStageImpl::new, responseValueTypeRef);
    }

    /** Internal {@link DispatchStage} implementation. */
    private final class DispatchStageImpl<V> implements DispatchStage<V> {

        private final Request request;
        private final ResponseConverter<V> responseConverter;

        private final AtomicBoolean wasDispatched = new AtomicBoolean(false);

        @Override
        public <S extends Sender> void dispatch(S sender, Dispatcher dispatcher, ApiHandler.OneArg<S, V> callback) {
            if (wasDispatched.getAndSet(true)) {
                throw new IllegalStateException("request was already dispatched");
            }

            OkHttpClient client = clientProvider.get(dispatcher);
            AdaptedCallback<S, V> adaptedCallback =
                    new AdaptedCallback<>(sender, responseConverter, dispatcher, callback);
            client.newCall(request).enqueue(adaptedCallback);
            dispatcher.dispatched();
        }

        private DispatchStageImpl(Request request, ResponseConverter<V> responseConverter) {
            this.request = request;
            this.responseConverter = responseConverter;
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
            } catch (Exception e) {
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
