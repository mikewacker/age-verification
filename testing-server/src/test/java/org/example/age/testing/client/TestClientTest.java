package org.example.age.testing.client;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;
import java.io.IOException;
import java.nio.ByteBuffer;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.example.age.testing.server.TestUndertowServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class TestClientTest {

    @RegisterExtension
    private static final TestUndertowServer server = TestUndertowServer.create(TestHandler::create);

    @Test
    public void get() throws IOException {
        Response response = TestClient.get(server.url("/path"));
        assertResponseBodyEquals(response, "GET /path");
    }

    @Test
    public void post_NoBody() throws IOException {
        Response response = TestClient.post(server.url("/path"));
        assertResponseBodyEquals(response, "POST /path");
    }

    @Test
    public void post_Body() throws IOException {
        Response response = TestClient.post(server.url("/path"), "test");
        assertResponseBodyEquals(response, "POST /path[application/json: test]");
    }

    @Test
    public void getInstance() {
        OkHttpClient client1 = TestClient.getInstance();
        OkHttpClient client2 = TestClient.getInstance();
        assertThat(client1).isSameAs(client2);
    }

    private static void assertResponseBodyEquals(Response response, String expectedResponseBody) throws IOException {
        assertThat(response.code()).isEqualTo(200);
        String responseBody = TestClient.readBody(response, new TypeReference<>() {});
        assertThat(responseBody).isEqualTo(expectedResponseBody);
    }

    /** Test {@link HttpHandler} that echoes back request details. */
    private static final class TestHandler implements HttpHandler {

        private static final ObjectMapper mapper = new ObjectMapper();

        private static HttpHandler create() {
            return new TestHandler();
        }

        @Override
        public void handleRequest(HttpServerExchange exchange) {
            exchange.getRequestReceiver().receiveFullBytes(TestHandler::handleRequest);
        }

        private static void handleRequest(HttpServerExchange exchange, byte[] rawRequestBody) {
            try {
                String method = exchange.getRequestMethod().toString();
                String path = exchange.getRequestPath();
                if (rawRequestBody.length == 0) {
                    String responseBody = String.format("%s %s", method, path);
                    sendResponse(exchange, responseBody);
                    return;
                }

                String contentType = exchange.getRequestHeaders().getFirst(Headers.CONTENT_TYPE);
                String requestBody = mapper.readValue(rawRequestBody, String.class);
                String responseBody = String.format("%s %s[%s: %s]", method, path, contentType, requestBody);
                sendResponse(exchange, responseBody);
            } catch (Exception e) {
                exchange.setStatusCode(StatusCodes.INTERNAL_SERVER_ERROR);
            }
        }

        private static void sendResponse(HttpServerExchange exchange, String responseBody)
                throws JsonProcessingException {
            byte[] rawResponseBody = mapper.writeValueAsBytes(responseBody);
            exchange.getResponseSender().send(ByteBuffer.wrap(rawResponseBody));
        }

        private TestHandler() {}
    }
}
