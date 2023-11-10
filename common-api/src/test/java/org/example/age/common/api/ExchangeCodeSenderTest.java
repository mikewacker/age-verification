package org.example.age.common.api;

import static org.assertj.core.api.Assertions.assertThat;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import java.io.IOException;
import okhttp3.Response;
import org.example.age.api.CodeSender;
import org.example.age.testing.client.TestClient;
import org.example.age.testing.server.TestUndertowServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class ExchangeCodeSenderTest {

    @RegisterExtension
    private static final TestUndertowServer server = TestUndertowServer.create(TestHandler::create);

    @Test
    public void send_Ok() throws IOException {
        send("/ok", 200);
    }

    @Test
    public void send_Error() throws IOException {
        send("/forbidden", 403);
    }

    @Test
    public void send_SendTwice() throws IOException {
        send("/send-twice", 200);
    }

    private void send(String path, int expectedStatusCode) throws IOException {
        Response response = TestClient.get(server.url(path));
        assertThat(response.code()).isEqualTo(expectedStatusCode);
    }

    /** Test {@link HttpHandler} that uses an {@link ExchangeCodeSender}. */
    private static final class TestHandler implements HttpHandler {

        public static HttpHandler create() {
            return new TestHandler();
        }

        @Override
        public void handleRequest(HttpServerExchange exchange) {
            CodeSender sender = ExchangeCodeSender.create(exchange);
            switch (exchange.getRequestPath()) {
                case "/ok" -> sender.sendOk();
                case "/forbidden" -> sender.send(StatusCodes.FORBIDDEN);
                case "/send-twice" -> sendTwice(sender);
                default -> sender.send(StatusCodes.NOT_FOUND);
            }
        }

        private static void sendTwice(CodeSender sender) {
            sender.sendOk();
            sender.send(StatusCodes.FORBIDDEN);
        }

        private TestHandler() {}
    }
}
