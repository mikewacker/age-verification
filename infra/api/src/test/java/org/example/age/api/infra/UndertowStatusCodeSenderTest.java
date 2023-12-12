package org.example.age.api.infra;

import static org.assertj.core.api.Assertions.assertThat;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import java.io.IOException;
import org.example.age.api.base.StatusCodeSender;
import org.example.age.testing.client.TestClient;
import org.example.age.testing.server.TestServer;
import org.example.age.testing.server.undertow.TestUndertowServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class UndertowStatusCodeSenderTest {

    @RegisterExtension
    private static final TestServer<?> server = TestUndertowServer.register("test", TestHandler::create);

    @Test
    public void send_Ok() throws IOException {
        int statusCode = executeRequest("/ok");
        assertThat(statusCode).isEqualTo(200);
    }

    @Test
    public void send_ErrorCode() throws IOException {
        int statusCode = executeRequest("/forbidden");
        assertThat(statusCode).isEqualTo(403);
    }

    @Test
    public void send_SendTwice() throws IOException {
        int statusCode = executeRequest("/send-twice");
        assertThat(statusCode).isEqualTo(200);
    }

    private static int executeRequest(String path) throws IOException {
        return TestClient.requestBuilder().get(server.url(path)).execute();
    }

    /** Test {@link HttpHandler} that uses an {@link UndertowStatusCodeSender}. */
    private static final class TestHandler implements HttpHandler {

        public static HttpHandler create() {
            return new TestHandler();
        }

        @Override
        public void handleRequest(HttpServerExchange exchange) {
            StatusCodeSender sender = UndertowStatusCodeSender.create(exchange);
            switch (exchange.getRequestPath()) {
                case "/ok" -> sender.sendOk();
                case "/forbidden" -> sender.send(StatusCodes.FORBIDDEN);
                case "/send-twice" -> sendTwice(sender);
                default -> sender.send(StatusCodes.NOT_FOUND);
            }
        }

        private static void sendTwice(StatusCodeSender sender) {
            sender.sendOk();
            sender.send(StatusCodes.FORBIDDEN);
        }

        private TestHandler() {}
    }
}
