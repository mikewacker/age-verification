package org.example.age.infra.service.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import org.example.age.api.Dispatcher;
import org.example.age.api.LiteHttpHandler;
import org.example.age.api.Sender;
import org.example.age.infra.service.client.internal.ExchangeClient;

@Singleton
final class RequestDispatcherImpl implements RequestDispatcher {

    private final ExchangeClient client;
    private final ObjectMapper mapper;

    @Inject
    public RequestDispatcherImpl(ExchangeClient client, ObjectMapper mapper) {
        this.client = client;
        this.mapper = mapper;
    }

    @Override
    public <S extends Sender> void dispatch(
            Request request, S sender, Dispatcher dispatcher, ResponseCallback<S> callback) {
        Call call = client.getInstance(dispatcher).newCall(request);
        Callback adaptedCallback = new AdaptedResponseCallback<>(sender, dispatcher, callback);
        call.enqueue(adaptedCallback);
        dispatcher.dispatched();
    }

    @Override
    public <B, S extends Sender> void dispatch(
            Request request,
            TypeReference<B> responseBodyTypeRef,
            S sender,
            Dispatcher dispatcher,
            ResponseBodyCallback<B, S> callback) {
        Call call = client.getInstance(dispatcher).newCall(request);
        Callback adaptedCallback =
                new AdaptedResponseBodyCallback<>(mapper, responseBodyTypeRef, sender, dispatcher, callback);
        call.enqueue(adaptedCallback);
        dispatcher.dispatched();
    }

    /** Sends a 502 error when the request fails. */
    private static void requestFailed(Sender sender) {
        sender.sendErrorCode(502);
    }

    /** Adapts a {@link ResponseCallback} to a {@link Callback}. */
    private record AdaptedResponseCallback<S extends Sender>(
            S sender, Dispatcher dispatcher, ResponseCallback<S> callback) implements Callback {

        @Override
        public void onResponse(Call call, Response response) {
            LiteHttpHandler<S> handler = new ResponseCallbackHandler<>(response, callback);
            dispatcher.executeHandler(sender, handler);
        }

        @Override
        public void onFailure(Call call, IOException e) {
            requestFailed(sender);
        }
    }

    /** Adapts a {@link ResponseBodyCallback} to a {@link Callback}. */
    private record AdaptedResponseBodyCallback<B, S extends Sender>(
            ObjectMapper mapper,
            TypeReference<B> responseBodyTypeRef,
            S sender,
            Dispatcher dispatcher,
            ResponseBodyCallback<B, S> callback)
            implements Callback {

        @Override
        public void onResponse(Call call, Response response) {
            LiteHttpHandler<S> handler =
                    new ResponseBodyCallbackHandler<>(response, mapper, responseBodyTypeRef, callback);
            dispatcher.executeHandler(sender, handler);
        }

        @Override
        public void onFailure(Call call, IOException e) {
            requestFailed(sender);
        }
    }

    /** {@link LiteHttpHandler} that calls a {@link ResponseCallback}. */
    private record ResponseCallbackHandler<S extends Sender>(Response response, ResponseCallback<S> callback)
            implements LiteHttpHandler<S> {

        @Override
        public void handleRequest(S sender, Dispatcher dispatcher) throws Exception {
            callback.onResponse(response, sender, dispatcher);
        }
    }

    /** {@link LiteHttpHandler} that calls a {@link ResponseBodyCallback}. */
    private record ResponseBodyCallbackHandler<B, S extends Sender>(
            Response response,
            ObjectMapper mapper,
            TypeReference<B> responseBodyTypeRef,
            ResponseBodyCallback<B, S> callback)
            implements LiteHttpHandler<S> {

        @Override
        public void handleRequest(S sender, Dispatcher dispatcher) throws Exception {
            if (!response.isSuccessful()) {
                callback.onResponse(response, null, sender, dispatcher);
                return;
            }

            Optional<B> maybeResponseBody = tryParseResponseBody(response, sender);
            if (maybeResponseBody.isEmpty()) {
                return;
            }
            B responseBody = maybeResponseBody.get();

            callback.onResponse(response, responseBody, sender, dispatcher);
        }

        /** Parses the body from the response, or sends an error. */
        private Optional<B> tryParseResponseBody(Response response, Sender sender) {
            try {
                B responseBody = mapper.readValue(response.body().bytes(), responseBodyTypeRef);
                return Optional.of(responseBody);
            } catch (IOException e) {
                requestFailed(sender);
                return Optional.empty();
            }
        }
    }
}
