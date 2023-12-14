package org.example.age.testing.server;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.NoSuchElementException;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class TestServerTest {

    @RegisterExtension
    private static final StubServer server = StubServer.register("test");

    @Test
    public void urls() {
        assertThat(server.host()).isEqualTo("localhost");
        int port = server.port();

        String expectedRootUrl = String.format("http://localhost:%d", port);
        assertThat(server.rootUrl()).isEqualTo(expectedRootUrl);
        String expectedUrl = String.format("http://localhost:%d/path?param=value", port);
        assertThat(server.url("/path?param=value")).isEqualTo(expectedUrl);
        assertThat(server.url("path?param=value")).isEqualTo(expectedUrl);
        assertThat(server.url("/path?param=%s", "value")).isEqualTo(expectedUrl);
    }

    @Test
    public void getTestServer() {
        TestServer<?> retrievedServer = TestServer.get("test");
        assertThat(server).isSameAs(retrievedServer);
    }

    @Test
    public void getUnderlyingServer() {
        assertThat(server.get()).isNotNull();
    }

    @Test
    public void error_serverNotStarted() {
        StubServer inactiveServer = StubServer.register("inactive");
        error_ServerNotStarted(inactiveServer::host);
        error_ServerNotStarted(inactiveServer::port);
        error_ServerNotStarted(inactiveServer::rootUrl);
        error_ServerNotStarted(() -> inactiveServer.url("/path"));
        error_ServerNotStarted(() -> inactiveServer.url("/path?param=%s", "value"));
        error_ServerNotStarted(inactiveServer::get);
    }

    private void error_ServerNotStarted(ThrowableAssert.ThrowingCallable callable) {
        assertThatThrownBy(callable).isInstanceOf(IllegalStateException.class).hasMessage("server has not started");
    }

    @Test
    public void error_GetUnregisteredServer() {
        assertThatThrownBy(() -> TestServer.get("unregistered"))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("server not found: unregistered");
    }

    @Test
    public void error_RegisterServerTwiceWithSameName() {
        StubServer.register("conflict");
        assertThatThrownBy(() -> StubServer.register("conflict"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("server already registered: conflict");
    }

    /** Stub {@link TestServer}. */
    private static final class StubServer extends TestServer<Object> {

        public static StubServer register(String name) {
            StubServer server = new StubServer();
            TestServer.register(name, server);
            return server;
        }

        @Override
        protected void start(Object server, int port) {}

        @Override
        protected void stop(Object server) {}

        private StubServer() {
            super(port -> new Object(), true);
        }
    }
}
