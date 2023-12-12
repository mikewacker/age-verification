package org.example.age.testing.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.age.testing.api.HttpOptionalAssert.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;
import java.io.IOException;
import org.example.age.api.base.HttpOptional;
import org.example.age.testing.server.TestServer;
import org.example.age.testing.server.undertow.TestUndertowServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class TestClientTest {

    @RegisterExtension
    private static final TestServer<?> server = TestUndertowServer.register("test", TestHandler::create);

    @Test
    public void exchange_StatusCodeResponse_Ok() throws IOException {
        int statusCode =
                TestClient.requestBuilder().get(server.url("/health-check")).execute();
        assertThat(statusCode).isEqualTo(200);
    }

    @Test
    public void exchange_StatusCodeResponse_ErrorCode() throws IOException {
        int statusCode =
                TestClient.requestBuilder().get(server.url("/not-found")).execute();
        assertThat(statusCode).isEqualTo(404);
    }

    @Test
    public void exchange_ValueResponse_Ok() throws IOException {
        HttpOptional<String> maybeGreeting = TestClient.requestBuilder(new TypeReference<String>() {})
                .get(server.url("/greeting"))
                .execute();
        assertThat(maybeGreeting).hasValue("Hello, world!");
    }

    @Test
    public void exchange_ValueResponse_ErrorCode() throws IOException {
        HttpOptional<String> maybeGreeting = TestClient.requestBuilder(new TypeReference<String>() {})
                .get(server.url("/not-found"))
                .execute();
        assertThat(maybeGreeting).isEmptyWithErrorCode(404);
    }

    /** Test {@link HttpHandler} for a JSON API. */
    private static final class TestHandler implements HttpHandler {

        public static HttpHandler create() {
            return new TestHandler();
        }

        @Override
        public void handleRequest(HttpServerExchange exchange) {
            switch (exchange.getRequestPath()) {
                case "/greeting":
                    exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                    exchange.getResponseSender().send("\"Hello, world!\"");
                    return;
                case "/health-check":
                    return;
                default:
                    exchange.setStatusCode(StatusCodes.NOT_FOUND);
            }
        }

        private TestHandler() {}
    }
}
