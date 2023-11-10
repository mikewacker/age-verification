package org.example.age.common.api.request.impl;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import java.io.IOException;
import java.util.Optional;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.example.age.api.JsonSender;
import org.example.age.common.api.ExchangeJsonSender;
import org.example.age.testing.client.TestClient;
import org.example.age.testing.server.TestUndertowServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class RequestParserTest {

    @RegisterExtension
    private static final TestUndertowServer server = TestUndertowServer.create(TestHandler::create);

    @Test
    public void exchange() throws IOException {
        exchange("/add?operand=2", "2", "4");
    }

    @Test
    public void error_ParamMissing() throws IOException {
        error("/add", "2", 400);
    }

    @Test
    public void error_InvalidParam() throws IOException {
        error("/add?operand=a", "2", 400);
    }

    @Test
    public void error_InvalidBody() throws IOException {
        error("/add?operand=2", "a", 400);
    }

    @Test
    public void error_UncaughtExceptionInCallback() throws IOException {
        error("/add?operand=200", "300", 500);
    }

    private void exchange(String path, String requestBody, String expectedResponseBody) throws IOException {
        Request request = createRequest(path, requestBody);
        Response response = TestClient.execute(request);
        assertThat(response.code()).isEqualTo(200);
        assertThat(response.body().string()).isEqualTo(expectedResponseBody);
    }

    private void error(String path, String requestBody, int expectedStatusCode) throws IOException {
        Request request = createRequest(path, requestBody);
        Response response = TestClient.execute(request);
        assertThat(response.code()).isEqualTo(expectedStatusCode);
    }

    private static Request createRequest(String path, String body) {
        String url = server.url(path);
        RequestBody requestBody = RequestBody.create(body, MediaType.get("application/json"));
        return new Request.Builder().url(url).post(requestBody).build();
    }

    /**
     * Test {@link HttpHandler} that uses a {@link RequestParser}.
     *
     * <p>It adds a number in the query parameter and a number in the body.</p>
     */
    private static final class TestHandler implements HttpHandler {

        private static final ObjectMapper mapper = new ObjectMapper();
        private static final TypeReference<Integer> INT_TYPE = new TypeReference<>() {};

        public static HttpHandler create() {
            return new TestHandler();
        }

        @Override
        public void handleRequest(HttpServerExchange exchange) {
            RequestParser parser = RequestParser.create(exchange, mapper);
            parser.parseBody(INT_TYPE, TestHandler::handleAddRequest);
        }

        private static void handleAddRequest(HttpServerExchange exchange, RequestParser parser, int operand2) {
            JsonSender<Integer> sender = ExchangeJsonSender.create(exchange, mapper);
            Optional<Integer> maybeOperand1 = parser.tryGetQueryParameter("operand", INT_TYPE);
            if (maybeOperand1.isEmpty()) {
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
