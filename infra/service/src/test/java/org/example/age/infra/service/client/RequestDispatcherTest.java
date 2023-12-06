package org.example.age.infra.service.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.age.testing.api.HttpOptionalAssert.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import dagger.Component;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import java.io.IOException;
import javax.inject.Singleton;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.SocketPolicy;
import org.example.age.api.Dispatcher;
import org.example.age.api.HttpOptional;
import org.example.age.api.JsonSender;
import org.example.age.api.StatusCodeSender;
import org.example.age.infra.api.ExchangeDispatcher;
import org.example.age.infra.api.ExchangeJsonSender;
import org.example.age.infra.api.ExchangeStatusCodeSender;
import org.example.age.testing.client.TestClient;
import org.example.age.testing.server.MockServer;
import org.example.age.testing.server.TestUndertowServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class RequestDispatcherTest {

    @RegisterExtension
    private static final TestUndertowServer frontendServer = TestUndertowServer.fromHandler(ProxyHandler::create);

    @RegisterExtension
    private static final MockServer backendServer = MockServer.create();

    @Test
    public void backendRequest_StatusCodeResponse_Ok() throws IOException {
        backendServer.enqueue(new MockResponse());
        int statusCode = executeRequestWithStatusCodeResponse();
        assertThat(statusCode).isEqualTo(200);
    }

    @Test
    public void backendRequest_StatusCodeResponse_ErrorCode() throws IOException {
        backendServer.enqueue(new MockResponse().setResponseCode(403));
        int statusCode = executeRequestWithStatusCodeResponse();
        assertThat(statusCode).isEqualTo(403);
    }

    @Test
    public void backendRequest_StatusCodeResponse_RequestFails() throws IOException {
        backendServer.enqueue(new MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AT_START));
        int statusCode = executeRequestWithStatusCodeResponse();
        assertThat(statusCode).isEqualTo(502);
    }

    @Test
    public void backendRequest_JsonResponse_Ok() throws IOException {
        backendServer.enqueue(new MockResponse().setBody("\"test\""));
        HttpOptional<String> maybeText = executeRequestWithJsonResponse();
        assertThat(maybeText).hasValue("test");
    }

    @Test
    public void backendRequest_JsonResponse_ErrorCode() throws IOException {
        backendServer.enqueue(new MockResponse().setResponseCode(403));
        HttpOptional<String> maybeText = executeRequestWithJsonResponse();
        assertThat(maybeText).isEmptyWithErrorCode(403);
    }

    @Test
    public void backendRequest_JsonResponse_RequestFails() throws IOException {
        backendServer.enqueue(new MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AT_START));
        HttpOptional<String> maybeText = executeRequestWithJsonResponse();
        assertThat(maybeText).isEmptyWithErrorCode(502);
    }

    @Test
    public void backendRequest_JsonResponse_ReadResponseBodyFails() throws IOException {
        backendServer.enqueue(new MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_DURING_RESPONSE_BODY));
        HttpOptional<String> maybeText = executeRequestWithJsonResponse();
        assertThat(maybeText).isEmptyWithErrorCode(502);
    }

    private int executeRequestWithStatusCodeResponse() throws IOException {
        return TestClient.apiRequestBuilder()
                .get(frontendServer.url("/status-code"))
                .executeWithStatusCodeResponse();
    }

    private HttpOptional<String> executeRequestWithJsonResponse() throws IOException {
        return TestClient.apiRequestBuilder()
                .get(frontendServer.url("/json"))
                .executeWithJsonResponse(new TypeReference<>() {});
    }

    /**
     * Test {@link HttpHandler} that uses a {@link RequestDispatcher}.
     *
     * It proxies the response it receives from a backend server.
     */
    @Singleton
    static final class ProxyHandler implements HttpHandler {

        private final RequestDispatcher requestDispatcher;

        public static HttpHandler create() {
            RequestDispatcher requestDispatcher = TestComponent.createRequestDispatcher();
            return new ProxyHandler(requestDispatcher);
        }

        @Override
        public void handleRequest(HttpServerExchange exchange) {
            switch (exchange.getRequestPath()) {
                case "/status-code" -> handleStatusCodeRequest(exchange);
                case "/json" -> handleJsonRequest(exchange);
                default -> ExchangeStatusCodeSender.create(exchange).sendErrorCode(StatusCodes.NOT_FOUND);
            }
        }

        private void handleStatusCodeRequest(HttpServerExchange exchange) {
            StatusCodeSender sender = ExchangeStatusCodeSender.create(exchange);
            Dispatcher dispatcher = ExchangeDispatcher.create(exchange);

            requestDispatcher
                    .requestBuilder(sender, dispatcher)
                    .get(backendServer.rootUrl())
                    .dispatchWithStatusCodeResponse(ProxyHandler::onStatusCodeResponseReceived);
        }

        private void handleJsonRequest(HttpServerExchange exchange) {
            JsonSender<String> sender = ExchangeJsonSender.create(exchange);
            Dispatcher dispatcher = ExchangeDispatcher.create(exchange);

            requestDispatcher
                    .requestBuilder(sender, dispatcher)
                    .get(backendServer.rootUrl())
                    .dispatchWithJsonResponse(new TypeReference<>() {}, ProxyHandler::onJsonResponseReceived);
        }

        private static void onStatusCodeResponseReceived(
                StatusCodeSender sender, int statusCode, Dispatcher dispatcher) {
            sender.send(statusCode);
        }

        private static void onJsonResponseReceived(
                JsonSender<String> sender, HttpOptional<String> maybeText, Dispatcher dispatcher) {
            sender.send(maybeText);
        }

        private ProxyHandler(RequestDispatcher requestDispatcher) {
            this.requestDispatcher = requestDispatcher;
        }
    }

    /** Dagger component that provides an {@link RequestDispatcher}. */
    @Component(modules = RequestDispatcherModule.class)
    @Singleton
    public interface TestComponent {

        static RequestDispatcher createRequestDispatcher() {
            TestComponent component = DaggerRequestDispatcherTest_TestComponent.create();
            return component.requestDispatcher();
        }

        RequestDispatcher requestDispatcher();
    }
}
