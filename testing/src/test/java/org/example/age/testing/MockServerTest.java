package org.example.age.testing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import okhttp3.Response;
import okhttp3.mockwebserver.MockResponse;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class MockServerTest {

    @RegisterExtension
    public static MockServer server = MockServer.create();

    @Test
    public void exchange() throws IOException {
        server.enqueue(new MockResponse().setBody("test"));
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
        String expectedUrl =
                String.format("http://localhost:%d", server.hostAndPort().getPort());
        assertThat(server.rootUrl()).isEqualTo(expectedUrl);
    }

    @Test
    public void error_ServerNotStarted() {
        MockServer inactiveServer = MockServer.create();
        error_ServerNotStarted(inactiveServer::get);
        error_ServerNotStarted(inactiveServer::hostAndPort);
        error_ServerNotStarted(inactiveServer::rootUrl);
        error_ServerNotStarted(() -> inactiveServer.enqueue(new MockResponse()));
    }

    private void error_ServerNotStarted(ThrowableAssert.ThrowingCallable callable) {
        assertThatThrownBy(callable).isInstanceOf(IllegalStateException.class).hasMessage("server has not started");
    }
}
