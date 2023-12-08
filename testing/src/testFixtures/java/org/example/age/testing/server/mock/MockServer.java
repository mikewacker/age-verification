package org.example.age.testing.server.mock;

import java.io.IOException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.example.age.testing.server.TestServer;

/**
 * Mock server backed by a {@link MockWebServer}.
 *
 * <p>A fresh server is started for each test.</p>
 */
public final class MockServer extends TestServer<MockWebServer> {

    /** Creates and registers a {@link MockServer}. */
    public static MockServer register(String name) {
        return new MockServer(name);
    }

    /** Enqueues a {@link MockResponse}. */
    public void enqueue(MockResponse response) {
        get().enqueue(response);
    }

    @Override
    protected void start(MockWebServer server, int port) throws IOException {
        server.start(port);
    }

    @Override
    protected void stop(MockWebServer server) throws IOException {
        server.shutdown();
    }

    private MockServer(String name) {
        super(name, MockServer::create, true);
    }

    /** Creates a {@link MockWebServer}. */
    private static MockWebServer create(int port) {
        return new MockWebServer();
    }
}
