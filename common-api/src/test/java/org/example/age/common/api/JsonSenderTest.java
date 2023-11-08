package org.example.age.common.api;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import java.io.IOException;
import okhttp3.Response;
import org.example.age.testing.client.TestClient;
import org.example.age.testing.server.TestUndertowServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class JsonSenderTest {

    @RegisterExtension
    private static final TestUndertowServer server = TestUndertowServer.create(TestHandler::create);

    @Test
    public void send_Body() throws IOException {
        sendBody("/body", "\"test\"");
    }

    @Test
    public void send_Error() throws IOException {
        sendError("/forbidden", 403);
    }

    @Test
    public void send_SendTwice() throws IOException {
        sendBody("/send-twice", "\"first\"");
    }

    @Test
    public void error_SerializationFailed() throws IOException {
        sendError("/serialization-failed", 500);
    }

    private void sendBody(String path, String expectedBody) throws IOException {
        Response response = TestClient.get(server.url(path));
        assertThat(response.code()).isEqualTo(200);
        assertThat(response.header("Content-Type")).isEqualTo("application/json");
        assertThat(response.body().string()).isEqualTo(expectedBody);
    }

    private void sendError(String path, int expectedStatusCode) throws IOException {
        Response response = TestClient.get(server.url(path));
        assertThat(response.code()).isEqualTo(expectedStatusCode);
        assertThat(response.header("Content-Type", "")).isEmpty();
        assertThat(response.body().string()).isEmpty();
    }

    /** Test {@link HttpHandler} that uses a {@link JsonSender}. */
    private static final class TestHandler implements HttpHandler {

        private static final ObjectMapper mapper = new ObjectMapper();

        public static HttpHandler create() {
            return new TestHandler();
        }

        @Override
        public void handleRequest(HttpServerExchange exchange) {
            JsonSender<String> sender = JsonSender.create(exchange, mapper);
            switch (exchange.getRequestPath()) {
                case "/body" -> sender.sendBody("test");
                case "/forbidden" -> sender.sendError(StatusCodes.FORBIDDEN);
                case "/send-twice" -> sendTwice(sender);
                case "/serialization-failed" -> serializationFailed(exchange);
                default -> sender.sendError(StatusCodes.NOT_FOUND);
            }
        }

        private static void sendTwice(JsonSender<String> sender) {
            sender.sendBody("first");
            sender.sendBody("second");
        }

        private static void serializationFailed(HttpServerExchange exchange) {
            JsonSender<HttpServerExchange> sender = JsonSender.create(exchange, mapper);
            sender.sendBody(exchange);
        }

        private TestHandler() {}
    }
}
