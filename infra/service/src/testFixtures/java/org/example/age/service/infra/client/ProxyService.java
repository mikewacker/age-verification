package org.example.age.service.infra.client;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.mikewacker.drift.api.Dispatcher;
import io.github.mikewacker.drift.api.HttpOptional;
import io.github.mikewacker.drift.api.Sender;
import io.undertow.server.HttpHandler;
import org.example.age.testing.server.TestServer;

/** Test service for {@link ProxyApi} that uses a {@link RequestDispatcher}. */
public final class ProxyService implements ProxyApi {

    private final RequestDispatcher requestDispatcher = RequestDispatcher.create();
    private final TestServer<?> backendServer = TestServer.get("backend");

    /** Creates an {@link HttpHandler} from a {@link ProxyService}. */
    static HttpHandler createHandler() {
        ProxyApi api = new ProxyService();
        return ProxyApi.createHandler(api);
    }

    @Override
    public void proxyStatusCode(Sender.StatusCode sender, Dispatcher dispatcher) {
        requestDispatcher
                .requestBuilder()
                .get(backendServer.rootUrl())
                .build()
                .dispatch(sender, dispatcher, this::handleStatusCodeResponse);
    }

    @Override
    public void proxyText(Sender.Value<String> sender, Dispatcher dispatcher) {
        requestDispatcher
                .requestBuilder(new TypeReference<String>() {})
                .get(backendServer.rootUrl())
                .build()
                .dispatch(sender, dispatcher, this::handleTextResponse);
    }

    /** Callback for a backend request whose response is a status code. */
    private void handleStatusCodeResponse(Sender.StatusCode sender, int statusCode, Dispatcher dispatcher) {
        sender.send(statusCode);
    }

    /** Callback for a backend request whose response is text. */
    private void handleTextResponse(
            Sender.Value<String> sender, HttpOptional<String> maybeText, Dispatcher dispatcher) {
        sender.send(maybeText);
    }

    private ProxyService() {}
}
