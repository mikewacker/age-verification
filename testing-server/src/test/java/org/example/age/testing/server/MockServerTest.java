package org.example.age.testing.server;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.example.age.testing.api.HttpOptionalAssert.assertThat;

import java.io.IOException;
import okhttp3.mockwebserver.MockResponse;
import org.assertj.core.api.ThrowableAssert;
import org.example.age.api.HttpOptional;
import org.example.age.testing.client.TestClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class MockServerTest {

    @RegisterExtension
    public static final MockServer server = MockServer.create();

    @Test
    public void exchange() throws IOException {
        server.enqueue(new MockResponse().setHeader("Content-Type", "text/html").setBody("<p>test</p>"));
        HttpOptional<String> maybeHtml = TestClient.getHtml(server.rootUrl());
        assertThat(maybeHtml).hasValue("<p>test</p>");
    }

    @Test
    public void getServer() {
        assertThat(server.get()).isNotNull();
    }

    @Test
    public void getLocation() {
        assertThat(server.host()).isEqualTo("localhost");
        String expectedUrl = String.format("http://localhost:%d", server.port());
        assertThat(server.rootUrl()).isEqualTo(expectedUrl);
    }

    @Test
    public void error_ServerNotStarted() {
        MockServer inactiveServer = MockServer.create();
        error_ServerNotStarted(inactiveServer::get);
        error_ServerNotStarted(inactiveServer::host);
        error_ServerNotStarted(inactiveServer::port);
        error_ServerNotStarted(inactiveServer::rootUrl);
        error_ServerNotStarted(() -> inactiveServer.enqueue(new MockResponse()));
    }

    private void error_ServerNotStarted(ThrowableAssert.ThrowingCallable callable) {
        assertThatThrownBy(callable).isInstanceOf(IllegalStateException.class).hasMessage("server has not started");
    }
}
