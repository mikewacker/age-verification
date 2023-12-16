package org.example.age.service.infra.client.internal;

import com.fasterxml.jackson.core.type.TypeReference;
import dagger.Component;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import java.io.IOException;
import java.util.Optional;
import javax.inject.Singleton;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.example.age.api.base.Dispatcher;
import org.example.age.api.base.Sender;
import org.example.age.api.infra.UndertowJsonApiHandler;
import org.example.age.client.infra.JsonApiClient;
import org.example.age.data.json.JsonValues;
import org.example.age.testing.server.TestServer;

/**
 * Test {@link HttpHandler} that uses a {@link DispatcherOkHttpClient}.
 *
 * It sends a greeting, making a backend call to get the recipient.
 */
public final class GreetingHandler implements HttpHandler {

    private final HttpHandler greetingHandler;

    private final DispatcherOkHttpClient client;
    private final TestServer<?> backendServer;

    public static HttpHandler create() {
        return new GreetingHandler();
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        greetingHandler.handleRequest(exchange);
    }

    private GreetingHandler() {
        greetingHandler =
                UndertowJsonApiHandler.builder(new TypeReference<String>() {}).build(this::handleGreetingRequest);

        client = TestComponent.createDispatcherOkHttpClient();
        backendServer = TestServer.get("backend");
    }

    private void handleGreetingRequest(Sender.Value<String> sender, Dispatcher dispatcher) {
        Callback callback = new RecipientCallback(sender, dispatcher);
        JsonApiClient.requestBuilder(client.get(dispatcher))
                .get(backendServer.rootUrl())
                .enqueue(callback);
        dispatcher.dispatched();
    }

    /** Callback for the backend request to get a recipient. */
    private record RecipientCallback(Sender.Value<String> sender, Dispatcher dispatcher) implements Callback {

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            dispatcher.executeHandler(() -> onResponse(response));
        }

        @Override
        public void onFailure(Call call, IOException e) {
            dispatcher.executeHandler(this::onFailure);
        }

        private void onResponse(Response response) {
            Optional<String> maybeRecipient = tryGetRecipient(response);
            if (maybeRecipient.isEmpty()) {
                onFailure();
                return;
            }
            String recipient = maybeRecipient.get();

            String greeting = String.format("Hello, %s!", recipient);
            sender.sendValue(greeting);
        }

        private void onFailure() {
            sender.sendErrorCode(502);
        }

        private static Optional<String> tryGetRecipient(Response response) {
            try {
                byte[] rawRecipient = response.body().bytes();
                return JsonValues.tryDeserialize(rawRecipient, new TypeReference<>() {});
            } catch (IOException e) {
                return Optional.empty();
            }
        }
    }

    /** Dagger component that provides a {@link DispatcherOkHttpClient}. */
    @Component(modules = DispatcherOkHttpClientModule.class)
    @Singleton
    interface TestComponent {

        static DispatcherOkHttpClient createDispatcherOkHttpClient() {
            TestComponent component = DaggerGreetingHandler_TestComponent.create();
            return component.dispatcherOkHttpClient();
        }

        DispatcherOkHttpClient dispatcherOkHttpClient();
    }
}
