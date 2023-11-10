package org.example.age.infra.service.client.internal;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Binds;
import dagger.Component;
import dagger.Module;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import java.io.IOException;
import javax.inject.Inject;
import javax.inject.Singleton;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.mockwebserver.MockResponse;
import org.example.age.api.Dispatcher;
import org.example.age.api.JsonSender;
import org.example.age.infra.api.ExchangeDispatcher;
import org.example.age.infra.api.ExchangeJsonSender;
import org.example.age.testing.client.TestClient;
import org.example.age.testing.server.MockServer;
import org.example.age.testing.server.TestUndertowServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class ExchangeClientTest {

    @RegisterExtension
    private static final TestUndertowServer frontendServer = TestUndertowServer.create(TestComponent::createHandler);

    @RegisterExtension
    private static final MockServer backendServer = MockServer.create();

    @Test
    public void exchange() throws IOException {
        backendServer.enqueue(new MockResponse().setBody("\"world\""));
        Response response = TestClient.get(frontendServer.rootUrl());
        assertThat(response.code()).isEqualTo(200);
        String greeting = TestClient.readBody(response, new TypeReference<>() {});
        assertThat(greeting).isEqualTo("Hello, world!");
    }

    /**
     * Test {@link HttpHandler} that uses an {@link ExchangeClient}.
     *
     * <p>It sends a greeting, making a backend call to get the recipient.</p>
     */
    @Singleton
    static final class TestHandler implements HttpHandler {

        private static final ObjectMapper mapper = new ObjectMapper();

        private final ExchangeClient client;

        @Inject
        public TestHandler(ExchangeClient client) {
            this.client = client;
        }

        @Override
        public void handleRequest(HttpServerExchange exchange) throws IOException {
            JsonSender<String> sender = ExchangeJsonSender.create(exchange, mapper);
            Dispatcher dispatcher = ExchangeDispatcher.create(exchange);

            Request request = new Request.Builder().url(backendServer.rootUrl()).build();
            Call call = client.getInstance(dispatcher).newCall(request);
            Callback callback = new RecipientCallback(sender, dispatcher);
            call.enqueue(callback);
            dispatcher.dispatched();
        }

        private record RecipientCallback(JsonSender<String> sender, Dispatcher dispatcher) implements Callback {

            @Override
            public void onResponse(Call call, Response response) {
                dispatcher.executeHandler(sender, (s, d) -> onRecipientReceived(response, s));
            }

            @Override
            public void onFailure(Call call, IOException e) {
                sender.sendError(StatusCodes.BAD_GATEWAY);
            }

            private static void onRecipientReceived(Response response, JsonSender<String> sender) throws IOException {
                String recipient = mapper.readValue(response.body().bytes(), new TypeReference<>() {});
                String greeting = String.format("Hello, %s!", recipient);
                sender.sendBody(greeting);
            }
        }
    }

    /** Dagger module that publishes a binding for {@link HttpHandler}, which uses an {@link ExchangeClient}. */
    @Module(includes = ExchangeClientModule.class)
    public interface TestModule {

        @Binds
        HttpHandler bindHandler(TestHandler impl);
    }

    /** Dagger component that provides an {@link HttpHandler}. */
    @Component(modules = TestModule.class)
    @Singleton
    public interface TestComponent {

        static HttpHandler createHandler() {
            TestComponent component = DaggerExchangeClientTest_TestComponent.create();
            return component.handler();
        }

        HttpHandler handler();
    }
}
