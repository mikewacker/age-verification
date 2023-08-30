package org.example.age.testing;

import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Random;
import java.util.function.Supplier;
import java.util.logging.Logger;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/** Undertow server that tests a custom handler. Starts before all tests and stops after all tests. */
public class TestServer implements BeforeAllCallback, AfterAllCallback {

    private static final Logger log = Logger.getLogger(TestServer.class.getName());

    private static final int NUM_ATTEMPTS = 3;
    private static final Random RANDOM = new Random();

    private final Supplier<HttpHandler> handlerFactory;

    private Undertow server = null;
    private int port = 0;
    private String rootUrl = null;

    /** Creates a test server with the provided handler. */
    public static TestServer create(HttpHandler handler) {
        return create(() -> handler);
    }

    /**
     * Creates a test server with a handler that's provided by the factory.
     *
     * <p>Used when creating the handler is not a trivial task.</p>
     */
    public static TestServer create(Supplier<HttpHandler> handlerFactory) {
        return new TestServer(handlerFactory);
    }

    /** Gets the port of the server. */
    public int getPort() {
        checkServerStarted();
        return port;
    }

    /** Gets the root URL of the server. */
    public String getRootUrl() {
        checkServerStarted();
        return rootUrl;
    }

    /** Gets a URL for the server at the specified path. */
    public String getUrl(String path) {
        checkServerStarted();
        path = path.startsWith("/") ? path : String.format("/%s", path);
        return String.format("%s%s", rootUrl, path);
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) {
        // Try multiple times, in case the port is already bound (or something else goes wrong).
        for (int i = 0; i < NUM_ATTEMPTS - 1; i++) {
            try {
                startServer();
                return;
            } catch (RuntimeException e) {
                log.warning("failed to start server, retrying...");
                log.warning(toExceptionString(e));
            }
        }
        startServer();
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) {
        if (server != null) {
            server.stop();
        }
    }

    /** Starts the server. */
    private void startServer() {
        HttpHandler handler = handlerFactory.get();
        port = RANDOM.nextInt(1024, 65536);
        server = Undertow.builder()
                .addHttpListener(port, "localhost")
                .setHandler(handler)
                .build();
        server.start();

        // Only set rootUrl after the server has started.
        rootUrl = String.format("http://localhost:%d", port);
    }

    /** Gets the message and stack trace as text. */
    private static String toExceptionString(Exception e) {
        StringWriter eWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(eWriter));
        return eWriter.toString();
    }

    /** Checks that the server has started. */
    private void checkServerStarted() {
        if (rootUrl == null) {
            throw new IllegalStateException("server has not started");
        }
    }

    private TestServer(Supplier<HttpHandler> handlerFactory) {
        this.handlerFactory = handlerFactory;
    }
}