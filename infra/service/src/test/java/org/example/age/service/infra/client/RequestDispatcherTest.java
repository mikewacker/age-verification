package org.example.age.service.infra.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.age.testing.api.HttpOptionalAssert.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import dagger.Component;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import java.io.IOException;
import javax.inject.Singleton;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.SocketPolicy;
import org.example.age.api.base.Dispatcher;
import org.example.age.api.base.HttpOptional;
import org.example.age.api.base.Sender;
import org.example.age.api.infra.UndertowJsonApiHandler;
import org.example.age.testing.client.TestClient;
import org.example.age.testing.server.TestServer;
import org.example.age.testing.server.mock.MockServer;
import org.example.age.testing.server.undertow.TestUndertowServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class RequestDispatcherTest {

    @RegisterExtension
    private static final TestServer<?> frontendServer = TestUndertowServer.register("frontend", ProxyHandler::create);

    @RegisterExtension
    private static final MockServer backendServer = MockServer.register("backend");

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
    public void backendRequest_JsonValueResponse_Ok() throws IOException {
        backendServer.enqueue(new MockResponse().setBody("\"test\""));
        HttpOptional<String> maybeText = executeRequestWithJsonValueResponse();
        assertThat(maybeText).hasValue("test");
    }

    @Test
    public void backendRequest_JsonValueResponse_ErrorCode() throws IOException {
        backendServer.enqueue(new MockResponse().setResponseCode(403));
        HttpOptional<String> maybeText = executeRequestWithJsonValueResponse();
        assertThat(maybeText).isEmptyWithErrorCode(403);
    }

    @Test
    public void backendRequest_JsonValueResponse_RequestFails() throws IOException {
        backendServer.enqueue(new MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AT_START));
        HttpOptional<String> maybeText = executeRequestWithJsonValueResponse();
        assertThat(maybeText).isEmptyWithErrorCode(502);
    }

    @Test
    public void backendRequest_JsonValueResponse_ReadResponseBodyFails() throws IOException {
        backendServer.enqueue(new MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_DURING_RESPONSE_BODY));
        HttpOptional<String> maybeText = executeRequestWithJsonValueResponse();
        assertThat(maybeText).isEmptyWithErrorCode(502);
    }

    private int executeRequestWithStatusCodeResponse() throws IOException {
        return TestClient.requestBuilder()
                .get(frontendServer.url("/status-code"))
                .execute();
    }

    private HttpOptional<String> executeRequestWithJsonValueResponse() throws IOException {
        return TestClient.requestBuilder(new TypeReference<String>() {})
                .get(frontendServer.url("/text"))
                .execute();
    }

    /**
     * Test {@link HttpHandler} that uses a {@link RequestDispatcher}.
     *
     * It proxies the response it receives from a backend server.
     */
    @Singleton
    static final class ProxyHandler implements HttpHandler {

        private final HttpHandler statusCodeHandler;
        private final HttpHandler textHandler;
        private final HttpHandler notFoundHandler;

        private final RequestDispatcher requestDispatcher;

        public static HttpHandler create() {
            return new ProxyHandler();
        }

        @Override
        public void handleRequest(HttpServerExchange exchange) throws Exception {
            switch (exchange.getRequestPath()) {
                case "/status-code" -> statusCodeHandler.handleRequest(exchange);
                case "/text" -> textHandler.handleRequest(exchange);
                default -> notFoundHandler.handleRequest(exchange);
            }
        }

        private ProxyHandler() {
            statusCodeHandler = UndertowJsonApiHandler.builder().build(this::proxyRequestWithStatusCodeResponse);
            textHandler = UndertowJsonApiHandler.builder(new TypeReference<String>() {})
                    .build(this::proxyRequestWithTextResponse);
            notFoundHandler = UndertowJsonApiHandler.notFound();

            requestDispatcher = TestComponent.createRequestDispatcher();
        }

        private void proxyRequestWithStatusCodeResponse(Sender.StatusCode sender, Dispatcher dispatcher) {
            requestDispatcher
                    .requestBuilder(dispatcher)
                    .get(backendServer.rootUrl())
                    .dispatch(sender, ProxyHandler::handleStatusCodeResponse);
        }

        private static void handleStatusCodeResponse(Sender.StatusCode sender, int statusCode, Dispatcher dispatcher) {
            sender.send(statusCode);
        }

        private void proxyRequestWithTextResponse(Sender.Value<String> sender, Dispatcher dispatcher) {
            requestDispatcher
                    .requestBuilder(dispatcher, new TypeReference<String>() {})
                    .get(backendServer.rootUrl())
                    .dispatch(sender, ProxyHandler::handleTextResponse);
        }

        private static void handleTextResponse(
                Sender.Value<String> sender, HttpOptional<String> maybeText, Dispatcher dispatcher) {
            sender.send(maybeText);
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
