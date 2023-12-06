package org.example.age.infra.api;

import static org.example.age.testing.api.HttpOptionalAssert.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import java.io.IOException;
import org.example.age.api.HttpOptional;
import org.example.age.api.JsonSender;
import org.example.age.testing.client.TestClient;
import org.example.age.testing.server.TestUndertowServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class ExchangeJsonSenderTest {

    @RegisterExtension
    private static final TestUndertowServer server = TestUndertowServer.fromHandler(TestHandler::create);

    @Test
    public void send_Body() throws IOException {
        HttpOptional<String> maybeValue = executeRequest("/value");
        assertThat(maybeValue).hasValue("test");
    }

    @Test
    public void send_ErrorCode() throws IOException {
        HttpOptional<String> maybeValue = executeRequest("/forbidden");
        assertThat(maybeValue).isEmptyWithErrorCode(403);
    }

    @Test
    public void send_SendTwice() throws IOException {
        HttpOptional<String> maybeValue = executeRequest("/send-twice");
        assertThat(maybeValue).hasValue("first");
    }

    @Test
    public void error_SerializationFailed() throws IOException {
        HttpOptional<String> maybeValue = executeRequest("/serialization-failed");
        assertThat(maybeValue).isEmptyWithErrorCode(500);
    }

    private static HttpOptional<String> executeRequest(String path) throws IOException {
        return TestClient.apiRequestBuilder().get(server.url(path)).executeWithJsonResponse(new TypeReference<>() {});
    }

    /** Test {@link HttpHandler} that uses an {@link ExchangeJsonSender}. */
    private static final class TestHandler implements HttpHandler {

        public static HttpHandler create() {
            return new TestHandler();
        }

        @Override
        public void handleRequest(HttpServerExchange exchange) {
            JsonSender<String> sender = ExchangeJsonSender.create(exchange);
            switch (exchange.getRequestPath()) {
                case "/value" -> sender.sendValue("test");
                case "/forbidden" -> sender.sendErrorCode(StatusCodes.FORBIDDEN);
                case "/send-twice" -> sendTwice(sender);
                case "/serialization-failed" -> serializationFailed(exchange);
                default -> sender.sendErrorCode(StatusCodes.NOT_FOUND);
            }
        }

        private static void sendTwice(JsonSender<String> sender) {
            sender.sendValue("first");
            sender.sendValue("second");
        }

        private static void serializationFailed(HttpServerExchange exchange) {
            JsonSender<HttpServerExchange> sender = ExchangeJsonSender.create(exchange);
            sender.sendValue(exchange);
        }

        private TestHandler() {}
    }
}
