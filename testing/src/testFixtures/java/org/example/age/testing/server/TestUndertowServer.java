package org.example.age.testing.server;

import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.PathHandler;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Random;
import java.util.logging.Logger;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * Test server backed by an {@link Undertow} server, which tests an {@link HttpHandler} (or a full server).
 *
 * <p>A single server is started for all tests.</p>
 */
public final class TestUndertowServer implements TestServer<Undertow>, BeforeAllCallback, AfterAllCallback {

    private static final Logger log = Logger.getLogger(TestUndertowServer.class.getName());

    private static final int NUM_ATTEMPTS = 3;
    private static final Random RANDOM = new Random();

    private final TestUndertowFactory serverFactory;

    private Undertow server = null;
    private int port = 0;
    private String rootUrl = null;

    /** Creates a test server from a root {@link HttpHandler}, which is created by a factory. */
    public static TestUndertowServer fromHandler(TestHandlerFactory handlerFactory) {
        return create(port -> TestUndertowServer.createServer(handlerFactory, port));
    }

    /**
     * Creates a test server from an {@link HttpHandler}, which is created by a factory.
     *
     * <p>The root handler will delegate to this handler for requests at the specified path.</p>
     */
    public static TestUndertowServer fromHandlerAtPath(TestHandlerFactory handlerFactory, String prefixPath) {
        return fromHandler(() -> createRootHandler(handlerFactory, prefixPath));
    }

    /** Creates a test server from an {@link Undertow} server, which is created by a factory. */
    public static TestUndertowServer create(TestUndertowFactory serverFactory) {
        return new TestUndertowServer(serverFactory);
    }

    @Override
    public Undertow get() {
        checkServerStarted();
        return server;
    }

    @Override
    public String host() {
        checkServerStarted();
        return "localhost";
    }

    @Override
    public int port() {
        checkServerStarted();
        return port;
    }

    @Override
    public String rootUrl() {
        checkServerStarted();
        return rootUrl;
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

        server = null;
        port = 0;
        rootUrl = null;
    }

    /** Creates a root {@link HttpHandler} that delegates to a handler for requests at the specified path. */
    private static HttpHandler createRootHandler(TestHandlerFactory handlerFactory, String prefixPath) {
        HttpHandler handler = handlerFactory.create();
        PathHandler rootHandler = new PathHandler();
        rootHandler.addPrefixPath(prefixPath, handler);
        return rootHandler;
    }

    /** Creates an {@link Undertow} server from a root {@link HttpHandler}, which is created by a factory. */
    private static Undertow createServer(TestHandlerFactory handlerFactory, int port) {
        HttpHandler handler = handlerFactory.create();
        return Undertow.builder()
                .addHttpListener(port, "localhost")
                .setHandler(handler)
                .build();
    }

    /** Starts the server. */
    private void startServer() {
        port = RANDOM.nextInt(1024, 65536);
        rootUrl = String.format("http://localhost:%d", port);
        server = serverFactory.create(port);
        server.start();
    }

    /** Gets the message and stack trace as text. */
    private static String toExceptionString(Exception e) {
        StringWriter eWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(eWriter));
        return eWriter.toString();
    }

    /** Checks that the server has started. */
    private void checkServerStarted() {
        if (server == null) {
            throw new IllegalStateException("server has not started");
        }
    }

    private TestUndertowServer(TestUndertowFactory serverFactory) {
        this.serverFactory = serverFactory;
    }
}
