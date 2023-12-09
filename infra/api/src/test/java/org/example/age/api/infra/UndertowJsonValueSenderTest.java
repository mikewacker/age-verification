package org.example.age.api.infra;

import static org.example.age.testing.api.HttpOptionalAssert.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import java.io.IOException;
import org.example.age.api.base.HttpOptional;
import org.example.age.api.base.ValueSender;
import org.example.age.testing.client.TestClient;
import org.example.age.testing.server.TestServer;
import org.example.age.testing.server.undertow.TestUndertowServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class UndertowJsonValueSenderTest {

    @RegisterExtension
    private static final TestServer<?> server = TestUndertowServer.register("test", TestHandler::create);

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

    private static HttpOptional<String> executeRequest(String path) throws IOException {
        return TestClient.apiRequestBuilder().get(server.url(path)).executeWithJsonResponse(new TypeReference<>() {});
    }

    /** Test {@link HttpHandler} that uses an {@link UndertowJsonValueSender}. */
    private static final class TestHandler implements HttpHandler {

        public static HttpHandler create() {
            return new TestHandler();
        }

        @Override
        public void handleRequest(HttpServerExchange exchange) {
            ValueSender<String> sender = UndertowJsonValueSender.create(exchange);
            switch (exchange.getRequestPath()) {
                case "/value" -> sender.sendValue("test");
                case "/forbidden" -> sender.sendErrorCode(StatusCodes.FORBIDDEN);
                case "/send-twice" -> sendTwice(sender);
                default -> sender.sendErrorCode(StatusCodes.NOT_FOUND);
            }
        }

        private static void sendTwice(ValueSender<String> sender) {
            sender.sendValue("first");
            sender.sendValue("second");
        }

        private TestHandler() {}
    }
}
