package org.example.age.infra.service.client;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import dagger.Binds;
import dagger.Component;
import dagger.Module;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import java.io.IOException;
import javax.inject.Inject;
import javax.inject.Singleton;
import okhttp3.Response;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.SocketPolicy;
import org.example.age.api.Dispatcher;
import org.example.age.api.JsonSender;
import org.example.age.api.JsonSerializer;
import org.example.age.api.StatusCodeSender;
import org.example.age.infra.api.ExchangeDispatcher;
import org.example.age.infra.api.ExchangeJsonSender;
import org.example.age.infra.api.ExchangeStatusCodeSender;
import org.example.age.test.data.TestMapperModule;
import org.example.age.testing.client.TestClient;
import org.example.age.testing.server.MockServer;
import org.example.age.testing.server.TestUndertowServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class RequestDispatcherTest {

    @RegisterExtension
    private static final TestUndertowServer frontendServer =
            TestUndertowServer.fromHandler(TestComponent::createHandler);

    @RegisterExtension
    private static final MockServer backendServer = MockServer.create();

    @Test
    public void backendRequestWithoutBody_Ok() throws IOException {
        backendServer.enqueue(new MockResponse());
        Response response = TestClient.get(frontendServer.url("/response"));
        assertThat(response.code()).isEqualTo(200);
    }

    @Test
    public void backendRequestWithoutBody_ErrorCode() throws IOException {
        backendServer.enqueue(new MockResponse().setResponseCode(403));
        Response response = TestClient.get(frontendServer.url("/response"));
        assertThat(response.code()).isEqualTo(403);
    }

    @Test
    public void backendRequestWithoutBody_RequestFails() throws IOException {
        backendServer.enqueue(new MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AT_START));
        Response response = TestClient.get(frontendServer.url("/response"));
        assertThat(response.code()).isEqualTo(502);
    }

    @Test
    public void backendRequestWithBody_Ok() throws IOException {
        backendServer.enqueue(new MockResponse().setBody("\"test\""));
        Response response = TestClient.get(frontendServer.url("/response-body"));
        assertThat(response.code()).isEqualTo(200);
        String responseBody = TestClient.readBody(response, new TypeReference<>() {});
        assertThat(responseBody).isEqualTo("test");
    }

    @Test
    public void backendRequestWithBody_ErrorCode() throws IOException {
        backendServer.enqueue(new MockResponse().setResponseCode(403));
        Response response = TestClient.get(frontendServer.url("/response-body"));
        assertThat(response.code()).isEqualTo(403);
    }

    @Test
    public void backendRequestWithBody_RequestFails() throws IOException {
        backendServer.enqueue(new MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AT_START));
        Response response = TestClient.get(frontendServer.url("/response-body"));
        assertThat(response.code()).isEqualTo(502);
    }

    @Test
    public void backendRequestWithBody_ReadResponseBodyFails() throws IOException {
        backendServer.enqueue(new MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_DURING_RESPONSE_BODY));
        Response response = TestClient.get(frontendServer.url("/response-body"));
        assertThat(response.code()).isEqualTo(502);
    }

    /**
     * Test {@link HttpHandler} that uses a {@link RequestDispatcher}.
     *
     * It proxies the response it receives from a backend server.
     */
    @Singleton
    static final class TestHandler implements HttpHandler {

        private final RequestDispatcher requestDispatcher;
        private final JsonSerializer serializer;

        @Inject
        public TestHandler(RequestDispatcher requestDispatcher, JsonSerializer serializer) {
            this.requestDispatcher = requestDispatcher;
            this.serializer = serializer;
        }

        @Override
        public void handleRequest(HttpServerExchange exchange) {
            switch (exchange.getRequestPath()) {
                case "/response" -> handleResponse(exchange);
                case "/response-body" -> handleResponseBody(exchange);
                default -> ExchangeStatusCodeSender.create(exchange).sendErrorCode(StatusCodes.NOT_FOUND);
            }
        }

        private void handleResponse(HttpServerExchange exchange) {
            StatusCodeSender sender = ExchangeStatusCodeSender.create(exchange);
            Dispatcher dispatcher = ExchangeDispatcher.create(exchange);

            requestDispatcher
                    .requestBuilder(backendServer.rootUrl(), sender, dispatcher)
                    .post()
                    .dispatchWithStatusCodeResponse(this::onResponseReceived);
        }

        private void handleResponseBody(HttpServerExchange exchange) {
            JsonSender<String> sender = ExchangeJsonSender.create(exchange, serializer);
            Dispatcher dispatcher = ExchangeDispatcher.create(exchange);

            requestDispatcher
                    .requestBuilder(backendServer.rootUrl(), sender, dispatcher)
                    .post()
                    .dispatchWithJsonResponse(new TypeReference<>() {}, this::onResponseBodyReceived);
        }

        private void onResponseReceived(StatusCodeSender sender, int statusCode, Dispatcher dispatcher) {
            sender.send(statusCode);
        }

        private void onResponseBodyReceived(
                JsonSender<String> sender, int statusCode, String responseBody, Dispatcher dispatcher) {
            if (statusCode != StatusCodes.OK) {
                sender.sendErrorCode(statusCode);
                return;
            }

            sender.sendBody(responseBody);
        }
    }

    /**
     * Dagger module that publishes a binding for {@link HttpHandler}, which uses a {@link RequestDispatcher}.
     *
     * <p>Also binds dependencies for {@link RequestDispatcher}.</p>
     */
    @Module(includes = {RequestDispatcherModule.class, TestMapperModule.class})
    interface TestModule {

        @Binds
        HttpHandler bindHttpHandler(TestHandler impl);
    }

    /** Dagger component that provides an {@link HttpHandler}. */
    @Component(modules = TestModule.class)
    @Singleton
    public interface TestComponent {

        static HttpHandler createHandler() {
            TestComponent component = DaggerRequestDispatcherTest_TestComponent.create();
            return component.handler();
        }

        HttpHandler handler();
    }
}
