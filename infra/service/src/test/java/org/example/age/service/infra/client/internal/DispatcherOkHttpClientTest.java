package org.example.age.service.infra.client.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.age.testing.api.HttpOptionalAssert.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import dagger.Component;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import java.io.IOException;
import java.util.Optional;
import javax.inject.Singleton;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.mockwebserver.MockResponse;
import org.example.age.api.base.Dispatcher;
import org.example.age.api.base.HttpOptional;
import org.example.age.api.base.Sender;
import org.example.age.api.infra.UndertowJsonApiHandler;
import org.example.age.data.json.JsonValues;
import org.example.age.testing.api.StubDispatcher;
import org.example.age.testing.client.TestClient;
import org.example.age.testing.server.TestServer;
import org.example.age.testing.server.mock.MockServer;
import org.example.age.testing.server.undertow.TestUndertowServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class DispatcherOkHttpClientTest {

    @RegisterExtension
    private static final TestServer<?> frontendServer =
            TestUndertowServer.register("frontend", GreetingHandler::create);

    @RegisterExtension
    private static final MockServer backendServer = MockServer.register("backend");

    @Test
    public void exchange() throws IOException {
        backendServer.enqueue(new MockResponse().setBody("\"world\""));
        HttpOptional<String> maybeGreeting = TestClient.requestBuilder(new TypeReference<String>() {})
                .get(frontendServer.rootUrl())
                .execute();
        assertThat(maybeGreeting).hasValue("Hello, world!");
    }

    @Test
    public void getSharedClient() throws IOException {
        DispatcherOkHttpClient sharedClient = TestComponent.createDispatcherOkHttpClient();
        OkHttpClient client1 = sharedClient.get(StubDispatcher.get());
        OkHttpClient client2 = sharedClient.get(StubDispatcher.get());
        assertThat(client1).isSameAs(client2);
    }

    /**
     * Test {@link HttpHandler} that uses an {@link DispatcherOkHttpClient}.
     *
     * <p>It sends a greeting, making a backend call to get the recipient.</p>
     */
    private static final class GreetingHandler implements HttpHandler {

        private final HttpHandler greetingHandler;

        private final DispatcherOkHttpClient client;

        public static HttpHandler create() {
            return new GreetingHandler();
        }

        @Override
        public void handleRequest(HttpServerExchange exchange) throws Exception {
            greetingHandler.handleRequest(exchange);
        }

        private GreetingHandler() {
            greetingHandler = UndertowJsonApiHandler.builder(new TypeReference<String>() {})
                    .build(this::sendGreeting);

            client = TestComponent.createDispatcherOkHttpClient();
        }

        private void sendGreeting(Sender.Value<String> sender, Dispatcher dispatcher) {
            Request request = new Request.Builder().url(backendServer.rootUrl()).build();
            Call call = client.get(dispatcher).newCall(request);
            Callback callback = new RecipientCallback(sender, dispatcher);
            call.enqueue(callback);
            dispatcher.dispatched();
        }

        private record RecipientCallback(Sender.Value<String> sender, Dispatcher dispatcher) implements Callback {

            @Override
            public void onResponse(Call call, Response response) {
                dispatcher.executeHandler(() -> onRecipientReceived(sender, response));
            }

            @Override
            public void onFailure(Call call, IOException e) {
                sender.sendErrorCode(StatusCodes.BAD_GATEWAY);
            }

            private static void onRecipientReceived(Sender.Value<String> sender, Response response) {
                Optional<String> maybeRecipient = tryGetRecipient(response);
                if (maybeRecipient.isEmpty()) {
                    sender.sendErrorCode(StatusCodes.BAD_GATEWAY);
                    return;
                }
                String recipient = maybeRecipient.get();

                String greeting = String.format("Hello, %s!", recipient);
                sender.sendValue(greeting);
            }

            private static Optional<String> tryGetRecipient(Response response) {
                try {
                    return JsonValues.tryDeserialize(response.body().bytes(), new TypeReference<>() {});
                } catch (IOException e) {
                    return Optional.empty();
                }
            }
        }
    }

    /** Dagger component that provides an {@link DispatcherOkHttpClient}. */
    @Component(modules = DispatcherOkHttpClientModule.class)
    @Singleton
    public interface TestComponent {

        static DispatcherOkHttpClient createDispatcherOkHttpClient() {
            TestComponent component = DaggerDispatcherOkHttpClientTest_TestComponent.create();
            return component.exchangeClient();
        }

        DispatcherOkHttpClient exchangeClient();
    }
}
