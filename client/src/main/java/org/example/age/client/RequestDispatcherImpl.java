package org.example.age.client;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.SameThreadExecutor;
import javax.inject.Inject;
import javax.inject.Singleton;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;

@Singleton
final class RequestDispatcherImpl implements RequestDispatcher {

    private final HttpServerExchangeClient client;

    @Inject
    public RequestDispatcherImpl(HttpServerExchangeClient client) {
        this.client = client;
    }

    @Override
    public void dispatch(Request request, SuccessCallback successCallback, HttpServerExchange exchange) {
        Call call = client.getInstance(exchange).newCall(request);
        Callback callback = UndertowCallback.create(successCallback, exchange);
        exchange.dispatch(SameThreadExecutor.INSTANCE, () -> call.enqueue(callback));
    }
}
