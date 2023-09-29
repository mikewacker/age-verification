package org.example.age.testing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.undertow.server.HttpServerExchange;
import java.io.IOException;
import okhttp3.Response;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class TestUndertowServerTest {

    @RegisterExtension
    private static final TestUndertowServer server = TestUndertowServer.create(TestUndertowServerTest::stubHandle);

    @Test
    public void exchange() throws IOException {
        Response response = TestClient.get(server.rootUrl());
        assertThat(response.code()).isEqualTo(200);
        assertThat(response.body().string()).isEqualTo("test");
    }

    @Test
    public void getServer() {
        assertThat(server.get()).isNotNull();
    }

    @Test
    public void getLocation() {
        assertThat(server.hostAndPort().getHost()).isEqualTo("localhost");
        assertThat(server.hostAndPort().getPort()).isBetween(1024, 65535);
        String expectedUrl =
                String.format("http://localhost:%d", server.hostAndPort().getPort());
        assertThat(server.rootUrl()).isEqualTo(expectedUrl);
    }

    @Test
    public void error_ServerNotStarted() {
        TestUndertowServer inactiveServer = TestUndertowServer.create(TestUndertowServerTest::stubHandle);
        error_ServerNotStarted(inactiveServer::get);
        error_ServerNotStarted(inactiveServer::hostAndPort);
        error_ServerNotStarted(inactiveServer::rootUrl);
    }

    private void error_ServerNotStarted(ThrowableAssert.ThrowingCallable callable) {
        assertThatThrownBy(callable).isInstanceOf(IllegalStateException.class).hasMessage("server has not started");
    }

    /** HTTP handler that sends a stub response. */
    private static void stubHandle(HttpServerExchange exchange) {
        exchange.getResponseSender().send("test");
    }
}
