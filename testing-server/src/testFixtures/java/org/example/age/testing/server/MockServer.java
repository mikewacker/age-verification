package org.example.age.testing.server;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * Test server backed by a {@link MockWebServer}.
 *
 * <p>A fresh server is started for each test.</p>
 */
public final class MockServer implements TestServer<MockWebServer>, BeforeEachCallback, AfterEachCallback {

    private MockWebServer server = null;
    private String rootUrl = null;

    /** Creates a mock server. */
    public static MockServer create() {
        return new MockServer();
    }

    @Override
    public MockWebServer get() {
        checkServerStarted();
        return server;
    }

    @Override
    public String host() {
        checkServerStarted();
        return server.getHostName();
    }

    @Override
    public int port() {
        checkServerStarted();
        return server.getPort();
    }

    @Override
    public String rootUrl() {
        checkServerStarted();
        return rootUrl;
    }

    /** Enqueues a {@link MockResponse} to be replayed by the underlying server. */
    public void enqueue(MockResponse response) {
        checkServerStarted();
        server.enqueue(response);
    }

    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        server = new MockWebServer();
        server.start();
        rootUrl = String.format("http://%s:%d", server.getHostName(), server.getPort());
    }

    @Override
    public void afterEach(ExtensionContext extensionContext) throws Exception {
        if (server != null) {
            server.shutdown();
        }

        server = null;
        rootUrl = null;
    }

    /** Checks that the server has started. */
    private void checkServerStarted() {
        if (server == null) {
            throw new IllegalStateException("server has not started");
        }
    }

    private MockServer() {}
}
