package org.example.age.testing.client;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.util.Map;
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
    public void post_UrlOnly() throws IOException {
        Response response = TestClient.post(server.url("/path"));
        assertResponseBodyEquals(response, "POST /path");
    }

    @Test
    public void post_HeadersAndBody() throws IOException {
        Response response = TestClient.post(server.url("/path"), Map.of("Cookie", "name=value"), "test");
        assertResponseBodyEquals(response, "POST /path[name=value][application/json: test]");
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
                StringWriter writer = new StringWriter();
                PrintWriter responseBodyWriter = new PrintWriter(writer);
                String method = exchange.getRequestMethod().toString();
                String path = exchange.getRequestPath();
                responseBodyWriter.format("%s %s", method, path);

                String userAgent = exchange.getRequestHeaders().getFirst(Headers.COOKIE);
                if (userAgent != null) {
                    responseBodyWriter.format("[%s]", userAgent);
                }

                if (rawRequestBody.length != 0) {
                    String contentType = exchange.getRequestHeaders().getFirst(Headers.CONTENT_TYPE);
                    String requestBody = mapper.readValue(rawRequestBody, String.class);
                    responseBodyWriter.format("[%s: %s]", contentType, requestBody);
                }

                String responseBody = writer.toString();
                byte[] rawResponseBody = mapper.writeValueAsBytes(responseBody);
                exchange.getResponseSender().send(ByteBuffer.wrap(rawResponseBody));
            } catch (Exception e) {
                exchange.setStatusCode(StatusCodes.INTERNAL_SERVER_ERROR);
            }
        }

        private TestHandler() {}
    }
}
