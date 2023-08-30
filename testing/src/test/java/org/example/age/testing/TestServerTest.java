package org.example.age.testing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class TestServerTest {

    @RegisterExtension
    private static final TestServer server = TestServer.create(TestServerTest::stubHandle);

    @Test
    public void exchange() throws IOException {
        OkHttpClient client = TestClient.getInstance();
        Request request = new Request.Builder().url(server.getRootUrl()).build();
        Response response = client.newCall(request).execute();
        assertThat(response.code()).isEqualTo(200);
        assertThat(response.header("Content-Type")).isEqualTo("text/plain");
        assertThat(response.body().string()).isEqualTo("test");
    }

    @Test
    public void getPort() {
        assertThat(server.getPort()).isBetween(1024, 65535);
    }

    @Test
    public void getRootUrl() {
        int port = server.getPort();
        String expectedUrl = String.format("http://localhost:%d", port);
        assertThat(server.getRootUrl()).isEqualTo(expectedUrl);
    }

    @Test
    public void getUrl() {
        int port = server.getPort();
        String expectedUrl = String.format("http://localhost:%d/test.html", port);
        assertThat(server.getUrl("/test.html")).isEqualTo(expectedUrl);
        assertThat(server.getUrl("test.html")).isEqualTo(expectedUrl);
    }

    @Test
    public void error_ServerNotStarted() {
        TestServer inactiveServer = TestServer.create(TestServerTest::stubHandle);
        error_ServerNotStarted(inactiveServer::getPort);
        error_ServerNotStarted(inactiveServer::getRootUrl);
        error_ServerNotStarted(() -> inactiveServer.getUrl("/test.html"));
    }

    private void error_ServerNotStarted(ThrowableAssert.ThrowingCallable callable) {
        assertThatThrownBy(callable)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("server has not started");
    }

    /** HTTP handler that sends a stub response. */
    private static void stubHandle(HttpServerExchange exchange) {
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
        exchange.getResponseSender().send("test");
    }
}
