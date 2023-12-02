package org.example.age.infra.api;

import static org.example.age.testing.api.HttpOptionalAssert.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import java.io.IOException;
import org.example.age.api.HttpOptional;
import org.example.age.api.JsonSender;
import org.example.age.api.JsonSerializer;
import org.example.age.testing.client.TestClient;
import org.example.age.testing.server.TestUndertowServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class RequestParserTest {

    @RegisterExtension
    private static final TestUndertowServer server = TestUndertowServer.fromHandler(TestHandler::create);

    @Test
    public void exchange() throws IOException {
        HttpOptional<Integer> maybeSum = executeAddRequest("/add?operand=2", 2);
        assertThat(maybeSum).hasValue(4);
    }

    @Test
    public void error_MissingParam() throws IOException {
        HttpOptional<Integer> maybeSum = executeAddRequest("/add", 2);
        assertThat(maybeSum).isEmptyWithErrorCode(400);
    }

    @Test
    public void error_InvalidParam() throws IOException {
        HttpOptional<Integer> maybeSum = executeAddRequest("/add?operand=a", 2);
        assertThat(maybeSum).isEmptyWithErrorCode(400);
    }

    @Test
    public void error_InvalidBody() throws IOException {
        HttpOptional<Integer> maybeSum = executeAddRequest("/add?operand=2", "a");
        assertThat(maybeSum).isEmptyWithErrorCode(400);
    }

    @Test
    public void error_UncaughtExceptionInCallback() throws IOException {
        HttpOptional<Integer> maybeSum = executeAddRequest("/add?operand=200", 300);
        assertThat(maybeSum).isEmptyWithErrorCode(500);
    }

    private static HttpOptional<Integer> executeAddRequest(String path, Object bodyOperand) throws IOException {
        return TestClient.apiRequestBuilder()
                .post(server.url(path))
                .body(bodyOperand)
                .executeWithJsonResponse(new TypeReference<>() {});
    }

    /**
     * Test {@link HttpHandler} that uses a {@link RequestParser}.
     *
     * <p>It adds a number in the query parameter and a number in the body.</p>
     */
    private static final class TestHandler implements HttpHandler {

        private static final JsonSerializer serializer = JsonSerializer.create(new ObjectMapper());

        public static HttpHandler create() {
            return new TestHandler();
        }

        @Override
        public void handleRequest(HttpServerExchange exchange) {
            RequestParser parser = RequestParser.create(exchange, serializer);
            parser.readBody(new TypeReference<>() {}, TestHandler::handleAddRequest);
        }

        private static void handleAddRequest(HttpServerExchange exchange, RequestParser parser, int operand2) {
            JsonSender<Integer> sender = ExchangeJsonSender.create(exchange, serializer);

            HttpOptional<Integer> maybeOperand1 = parser.tryGetQueryParameter("operand", new TypeReference<>() {});
            if (maybeOperand1.isEmpty()) {
                sender.sendErrorCode(maybeOperand1.statusCode());
                return;
            }
            int operand1 = maybeOperand1.get();

            int sum = operand1 + operand2;
            if (sum == 500) {
                throw new RuntimeException();
            }

            sender.sendBody(sum);
        }

        private TestHandler() {}
    }
}
