package org.example.age.infra.service.client.internal;

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
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.mockwebserver.MockResponse;
import org.example.age.api.base.Dispatcher;
import org.example.age.api.base.HttpOptional;
import org.example.age.api.base.ValueSender;
import org.example.age.api.infra.UndertowDispatcher;
import org.example.age.api.infra.UndertowJsonValueSender;
import org.example.age.data.json.JsonValues;
import org.example.age.testing.client.TestClient;
import org.example.age.testing.server.TestServer;
import org.example.age.testing.server.mock.MockServer;
import org.example.age.testing.server.undertow.TestUndertowServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class ExchangeClientTest {

    @RegisterExtension
    private static final TestServer<?> frontendServer =
            TestUndertowServer.register("frontend", GreetingHandler::create);

    @RegisterExtension
    private static final MockServer backendServer = MockServer.register("backend");

    @Test
    public void exchange() throws IOException {
        backendServer.enqueue(new MockResponse().setBody("\"world\""));
        HttpOptional<String> maybeGreeting = TestClient.apiRequestBuilder()
                .get(frontendServer.rootUrl())
                .executeWithJsonResponse(new TypeReference<>() {});
        assertThat(maybeGreeting).hasValue("Hello, world!");
    }

    /**
     * Test {@link HttpHandler} that uses an {@link ExchangeClient}.
     *
     * <p>It sends a greeting, making a backend call to get the recipient.</p>
     */
    @Singleton
    static final class GreetingHandler implements HttpHandler {

        private final ExchangeClient client;

        public static HttpHandler create() {
            ExchangeClient client = TestComponent.createExchangeClient();
            return new GreetingHandler(client);
        }

        @Override
        public void handleRequest(HttpServerExchange exchange) {
            ValueSender<String> sender = UndertowJsonValueSender.create(exchange);
            Dispatcher dispatcher = UndertowDispatcher.create(exchange);

            Request request = new Request.Builder().url(backendServer.rootUrl()).build();
            Call call = client.getInstance(dispatcher).newCall(request);
            Callback callback = new RecipientCallback(sender, dispatcher);
            call.enqueue(callback);
            dispatcher.dispatched();
        }

        private record RecipientCallback(ValueSender<String> sender, Dispatcher dispatcher) implements Callback {

            @Override
            public void onResponse(Call call, Response response) {
                dispatcher.executeHandler(sender, (s, d) -> onRecipientReceived(s, response));
            }

            @Override
            public void onFailure(Call call, IOException e) {
                sender.sendErrorCode(StatusCodes.BAD_GATEWAY);
            }

            private static void onRecipientReceived(ValueSender<String> sender, Response response) throws IOException {
                Optional<String> maybeRecipient =
                        JsonValues.tryDeserialize(response.body().bytes(), new TypeReference<>() {});
                if (maybeRecipient.isEmpty()) {
                    sender.sendErrorCode(StatusCodes.BAD_GATEWAY);
                    return;
                }
                String recipient = maybeRecipient.get();

                String greeting = String.format("Hello, %s!", recipient);
                sender.sendValue(greeting);
            }
        }

        private GreetingHandler(ExchangeClient client) {
            this.client = client;
        }
    }

    /** Dagger component that provides an {@link ExchangeClient}. */
    @Component(modules = ExchangeClientModule.class)
    @Singleton
    public interface TestComponent {

        static ExchangeClient createExchangeClient() {
            TestComponent component = DaggerExchangeClientTest_TestComponent.create();
            return component.exchangeClient();
        }

        ExchangeClient exchangeClient();
    }
}
