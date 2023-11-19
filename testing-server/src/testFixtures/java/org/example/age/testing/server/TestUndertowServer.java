package org.example.age.testing.server;

import com.google.common.net.HostAndPort;
import dagger.BindsInstance;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Random;
import java.util.logging.Logger;
import javax.inject.Named;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/** Undertow server that tests a custom handler, or a full server. A single server is started for all tests. */
public final class TestUndertowServer implements TestServer<Undertow>, BeforeAllCallback, AfterAllCallback {

    private static final Logger log = Logger.getLogger(TestUndertowServer.class.getName());

    private static final int NUM_ATTEMPTS = 3;
    private static final Random RANDOM = new Random();

    private final ServerFactory serverFactory;

    private Undertow server = null;
    private HostAndPort hostAndPort = null;
    private String rootUrl = null;

    /** Creates a test server from the root {@link HttpHandler}, which is created by a factory. */
    public static TestUndertowServer create(HandlerFactory handlerFactory) {
        return create(port -> TestUndertowServer.createServer(handlerFactory, port));
    }

    /** Creates a test server from an {@link Undertow} server, which is created by a factory. */
    public static TestUndertowServer create(ServerFactory serverFactory) {
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

    /** Creates an {@link Undertow} server from the root {@link HttpHandler}, which is created by a factory. */
    private static Undertow createServer(HandlerFactory handlerFactory, int port) {
        HttpHandler handler = handlerFactory.create();
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

    private TestUndertowServer(ServerFactory serverFactory) {
        this.serverFactory = serverFactory;
    }

    /** Factory that creates the root {@link HttpHandler}. */
    @FunctionalInterface
    public interface HandlerFactory {

        HttpHandler create();
    }

    /** Factory that creates an {@link Undertow} server that listens on the specified port. */
    @FunctionalInterface
    public interface ServerFactory {

        Undertow create(int port);
    }

    /**
     * Dagger component that provides the root {@link HttpHandler}.
     *
     * <p>Implementations will need to extend and annotate this interface.</p>
     */
    public interface HandlerComponent {

        HttpHandler handler();
    }

    /**
     * Dagger component that provides an {@link Undertow} server.
     *
     * <p>Implementations will need to extend and annotate this interface (and the factory sub-interface).</p>
     */
    public interface ServerComponent {

        Undertow server();

        interface Factory<C extends ServerComponent> {

            C create(@BindsInstance @Named("port") int port);
        }
    }
}
