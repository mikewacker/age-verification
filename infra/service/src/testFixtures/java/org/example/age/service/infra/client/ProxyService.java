package org.example.age.service.infra.client;

import com.fasterxml.jackson.core.type.TypeReference;
import dagger.Component;
import io.undertow.server.HttpHandler;
import javax.inject.Singleton;
import org.example.age.api.base.Dispatcher;
import org.example.age.api.base.HttpOptional;
import org.example.age.api.base.Sender;
import org.example.age.testing.server.TestServer;

/** Test service for {@link ProxyApi} that uses a {@link RequestDispatcher}. */
public final class ProxyService implements ProxyApi {

    private final RequestDispatcher requestDispatcher = TestComponent.createRequestDispatcher();
    private final TestServer<?> backendServer = TestServer.get("backend");

    /** Creates an {@link HttpHandler} from a {@link ProxyService}. */
    static HttpHandler createHandler() {
        ProxyApi api = new ProxyService();
        return ProxyApi.createHandler(api);
    }

    @Override
    public void proxyStatusCode(Sender.StatusCode sender, Dispatcher dispatcher) {
        requestDispatcher
                .requestBuilder(dispatcher)
                .get(backendServer.rootUrl())
                .dispatch(sender, this::handleStatusCodeResponse);
    }

    @Override
    public void proxyText(Sender.Value<String> sender, Dispatcher dispatcher) {
        requestDispatcher
                .requestBuilder(dispatcher, new TypeReference<String>() {})
                .get(backendServer.rootUrl())
                .dispatch(sender, this::handleTextResponse);
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

    /** Dagger component that provides a {@link RequestDispatcher}. */
    @Component(modules = RequestDispatcherModule.class)
    @Singleton
    interface TestComponent {

        static RequestDispatcher createRequestDispatcher() {
            TestComponent component = DaggerProxyService_TestComponent.create();
            return component.requestDispatcher();
        }

        RequestDispatcher requestDispatcher();
    }
}
