package org.example.age.testing.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.age.testing.api.HttpOptionalAssert.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;
import java.io.IOException;
import org.example.age.api.HttpOptional;
import org.example.age.testing.server.TestUndertowServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class TestClientTest {

    @RegisterExtension
    private static final TestUndertowServer server = TestUndertowServer.fromHandler(TestHandler::create);

    @Test
    public void getHtml_Ok() throws IOException {
        HttpOptional<String> maybeHtml = TestClient.getHtml(server.url("/greeting.html"));
        assertThat(maybeHtml).hasValue("<p>Hello, world!</p>");
    }

    @Test
    public void getHtml_ErrorCode() throws IOException {
        HttpOptional<String> maybeHtml = TestClient.getHtml(server.url("/not-found.html"));
        assertThat(maybeHtml).isEmptyWithErrorCode(404);
    }

    @Test
    public void executeApiRequest_StatusCodeResponse_Ok() throws IOException {
        int statusCode = TestClient.apiRequestBuilder()
                .get(server.url("/api/health-check"))
                .executeWithStatusCodeResponse();
        assertThat(statusCode).isEqualTo(200);
    }

    @Test
    public void executeApiRequest_StatusCodeResponse_ErrorCode() throws IOException {
        int statusCode =
                TestClient.apiRequestBuilder().get(server.url("/api/not-found")).executeWithStatusCodeResponse();
        assertThat(statusCode).isEqualTo(404);
    }

    @Test
    public void executeApiRequest_JsonResponse_Ok() throws IOException {
        HttpOptional<String> maybeGreeting = TestClient.apiRequestBuilder()
                .get(server.url("/api/greeting"))
                .executeWithJsonResponse(new TypeReference<>() {});
        assertThat(maybeGreeting).hasValue("Hello, world!");
    }

    @Test
    public void executeApiRequest_JsonResponse_ErrorCode() throws IOException {
        HttpOptional<String> maybeGreeting = TestClient.apiRequestBuilder()
                .get(server.url("/api/not-found"))
                .executeWithJsonResponse(new TypeReference<>() {});
        assertThat(maybeGreeting).isEmptyWithErrorCode(404);
    }

    /** Test {@link HttpHandler} that echoes back request details. */
    private static final class TestHandler implements HttpHandler {

        private static HttpHandler create() {
            return new TestHandler();
        }

        @Override
        public void handleRequest(HttpServerExchange exchange) {
            switch (exchange.getRequestPath()) {
                case "/greeting.html":
                    exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/html");
                    exchange.getResponseSender().send("<p>Hello, world!</p>");
                    return;
                case "/api/greeting":
                    exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                    exchange.getResponseSender().send("\"Hello, world!\"");
                    return;
                case "/api/health-check":
                    return;
                default:
                    exchange.setStatusCode(StatusCodes.NOT_FOUND);
            }
        }

        private TestHandler() {}
    }
}
