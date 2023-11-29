package org.example.age.testing.server;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class TestServerTest {

    @Test
    public void url() {
        TestServer<?> server = StubServer.create();
        String expectedUrl = "http://localhost/path";
        assertThat(server.url("/path")).isEqualTo(expectedUrl);
        assertThat(server.url("path")).isEqualTo(expectedUrl);
    }

    @Test
    public void url_Format() {
        TestServer<?> server = StubServer.create();
        assertThat(server.url("/path?name=%s", "value")).isEqualTo("http://localhost/path?name=value");
    }

    /** Stub test server with a root URL. */
    private static final class StubServer implements TestServer<Void> {

        public static TestServer<?> create() {
            return new StubServer();
        }

        @Override
        public Void get() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String host() {
            throw new UnsupportedOperationException();
        }

        @Override
        public int port() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String rootUrl() {
            return "http://localhost";
        }

        private StubServer() {}
    }
}
