package org.example.age.testing.server;

import com.google.common.net.HostAndPort;
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

/** Undertow server that tests a custom handler, or a full server. A single server is started for all tests. */
public final class TestUndertowServer implements TestServer<Undertow>, BeforeAllCallback, AfterAllCallback {

    private static final Logger log = Logger.getLogger(TestUndertowServer.class.getName());

    private static final int NUM_ATTEMPTS = 3;
    private static final Random RANDOM = new Random();

    private final Factory serverFactory;

    private Undertow server = null;
    private HostAndPort hostAndPort = null;
    private String rootUrl = null;

    /** Creates a test server with the provided handler. */
    public static TestUndertowServer create(HttpHandler handler) {
        return create(() -> handler);
    }

    /**
     * Creates a test server with a handler that's provided by the factory.
     *
     * <p>Used when creating the handler is not a trivial task.</p>
     */
    public static TestUndertowServer create(Supplier<HttpHandler> handlerFactory) {
        return create((int port) -> TestUndertowServer.createServer(handlerFactory, port));
    }

    /** Creates a test server using the provided factory. */
    public static TestUndertowServer create(Factory serverFactory) {
        return new TestUndertowServer(serverFactory);
    }

    @Override
    public Undertow get() {
        checkServerStarted();
        return server;
    }

    @Override
    public HostAndPort hostAndPort() {
        checkServerStarted();
        return hostAndPort;
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
        hostAndPort = null;
        rootUrl = null;
    }

    /** Creates a server with a handler that's provided by the factory. */
    private static Undertow createServer(Supplier<HttpHandler> handlerFactory, int port) {
        HttpHandler handler = handlerFactory.get();
        return Undertow.builder()
                .addHttpListener(port, "localhost")
                .setHandler(handler)
                .build();
    }

    /** Starts the server. */
    private void startServer() {
        int port = RANDOM.nextInt(1024, 65536);
        server = serverFactory.create(port);
        server.start();
        hostAndPort = HostAndPort.fromParts("localhost", port);
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
        if (server == null) {
            throw new IllegalStateException("server has not started");
        }
    }

    private TestUndertowServer(Factory serverFactory) {
        this.serverFactory = serverFactory;
    }

    /** Creates a server that listens on the specified port. */
    @FunctionalInterface
    public interface Factory {

        Undertow create(int port);
    }
}
