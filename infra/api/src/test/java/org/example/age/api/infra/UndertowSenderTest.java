package org.example.age.api.infra;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.age.testing.api.HttpOptionalAssert.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import java.io.IOException;
import org.example.age.api.base.HttpOptional;
import org.example.age.api.base.Sender;
import org.example.age.testing.client.TestClient;
import org.example.age.testing.server.TestServer;
import org.example.age.testing.server.undertow.TestUndertowServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class UndertowSenderTest {

    @RegisterExtension
    private static final TestServer<?> server = TestUndertowServer.register("test", TestHandler::create);

    @Test
    public void send_StatusCode_Ok() throws IOException {
        int statusCode = executeStatusCodeRequest("/ok");
        assertThat(statusCode).isEqualTo(200);
    }

    @Test
    public void send_StatusCode_Forbidden() throws IOException {
        int statusCode = executeStatusCodeRequest("/forbidden");
        assertThat(statusCode).isEqualTo(403);
    }

    @Test
    public void send_StatusCode_SendTwice() throws IOException {
        int statusCode = executeStatusCodeRequest("/ok-twice");
        assertThat(statusCode).isEqualTo(200);
    }

    @Test
    public void send_JsonValue_Ok() throws IOException {
        HttpOptional<String> maybeText = executeTextRequest("/text");
        assertThat(maybeText).hasValue("test");
    }

    @Test
    public void send_JsonValue_Forbidden() throws IOException {
        HttpOptional<String> maybeText = executeTextRequest("/text-forbidden");
        assertThat(maybeText).isEmptyWithErrorCode(403);
    }

    @Test
    public void send_JsonValue_SendTwice() throws IOException {
        HttpOptional<String> maybeText = executeTextRequest("/text-twice");
        assertThat(maybeText).hasValue("first");
    }

    private static int executeStatusCodeRequest(String path) throws IOException {
        return TestClient.requestBuilder().get(server.url(path)).execute();
    }

    private static HttpOptional<String> executeTextRequest(String path) throws IOException {
        return TestClient.requestBuilder(new TypeReference<String>() {})
                .get(server.url(path))
                .execute();
    }

    /** Test {@link HttpHandler} that uses an {@link UndertowSender}. */
    private static final class TestHandler implements HttpHandler {

        public static HttpHandler create() {
            return new TestHandler();
        }

        @Override
        public void handleRequest(HttpServerExchange exchange) {
            Sender.StatusCode statusCodeSender = UndertowSender.StatusCode.create(exchange);
            Sender.Value<String> textSender = UndertowSender.JsonValue.create(exchange);
            switch (exchange.getRequestPath()) {
                case "/ok" -> statusCodeSender.sendOk();
                case "/forbidden" -> statusCodeSender.sendErrorCode(StatusCodes.FORBIDDEN);
                case "/ok-twice" -> sendStatusCodeTwice(statusCodeSender);
                case "/text" -> textSender.sendValue("test");
                case "/text-forbidden" -> textSender.sendErrorCode(StatusCodes.FORBIDDEN);
                case "/text-twice" -> sendTextTwice(textSender);
                default -> statusCodeSender.sendErrorCode(StatusCodes.NOT_FOUND);
            }
        }

        private static void sendStatusCodeTwice(Sender.StatusCode sender) {
            sender.sendOk();
            sender.sendErrorCode(StatusCodes.FORBIDDEN);
        }

        private static void sendTextTwice(Sender.Value<String> sender) {
            sender.sendValue("first");
            sender.sendValue("second");
        }

        private TestHandler() {}
    }
}
