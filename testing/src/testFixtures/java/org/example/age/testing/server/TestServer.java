package org.example.age.testing.server;

import com.google.errorprone.annotations.FormatMethod;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * Extension that starts a server on localhost.
 *
 * <p>This extension will be a static field, though some implementations may start a fresh server for each test.</p>
 *
 * <p>Implementations will provide static {@code register()} method(s) to create and register a {@link TestServer}.</p>
 */
public abstract class TestServer<T>
        implements BeforeAllCallback, BeforeEachCallback, AfterEachCallback, AfterAllCallback {

    // Maintaining a static table of test servers could create issues if tests are run in parallel.
    private static final Map<String, TestServer<?>> servers = new ConcurrentHashMap<>();

    private final TestServerFactory<T> serverFactory;
    private final boolean createForEachTest;

    private T server;
    private int port;
    private String rootUrl;

    /** Gets a registered test server. */
    public static TestServer<?> get(String name) {
        TestServer<?> server = servers.get(name);
        if (server == null) {
            String message = String.format("server not found: %s", name);
            throw new NoSuchElementException(message);
        }

        return server;
    }

    /** Gets the host of the server. */
    public final String host() {
        checkServerStarted();
        return "localhost";
    }

    /** Gets the port of the server. */
    public final int port() {
        checkServerStarted();
        return port;
    }

    /** Gets the root URL of the server. */
    public final String rootUrl() {
        checkServerStarted();
        return rootUrl;
    }

    /** Gets the URL at the specified path. */
    @SuppressWarnings("FormatStringAnnotation")
    public final String url(String path) {
        return url(path, new Object[0]);
    }

    /** Gets the URL at the specified path. */
    @FormatMethod
    public final String url(String pathFormat, Object... args) {
        pathFormat = pathFormat.replaceFirst("^/", "");
        String path = String.format(pathFormat, args);
        return String.format("%s/%s", rootUrl(), path);
    }

    /** Gets the underlying server. */
    public final T get() {
        checkServerStarted();
        return server;
    }

    @Override
    public final void beforeAll(ExtensionContext context) throws Exception {
        if (!createForEachTest) {
            createAndStart();
        }
    }

    @Override
    public final void beforeEach(ExtensionContext context) throws Exception {
        if (createForEachTest) {
            createAndStart();
        }
    }

    @Override
    public final void afterEach(ExtensionContext context) throws Exception {
        if (createForEachTest) {
            stop();
        }
    }

    @Override
    public final void afterAll(ExtensionContext context) throws Exception {
        try {
            if (!createForEachTest) {
                stop();
            }
        } finally {
            servers.clear();
        }
    }

    /** Creates and registers a {@link TestServer}, specifying whether a fresh server is created for each test. */
    protected TestServer(String name, TestServerFactory<T> serverFactory, boolean createForEachTest) {
        this.serverFactory = serverFactory;
        this.createForEachTest = createForEachTest;
        clear();
        register(name);
    }

    /** Starts the server. */
    protected abstract void start(T server, int port) throws Exception;

    /** Stops the server. */
    protected abstract void stop(T server) throws Exception;

    /** Registers this {@link TestServer}. */
    private void register(String name) {
        TestServer<?> conflictingServer = servers.putIfAbsent(name, this);
        if (conflictingServer != null) {
            String message = String.format("server already registered: %s", name);
            throw new IllegalStateException(message);
        }
    }

    /** Creates and starts a fresh server. */
    private void createAndStart() throws Exception {
        try {
            port = getAvailablePort();
            rootUrl = String.format("http://localhost:%d", port);
            server = serverFactory.create(port);
            start(server, port);
        } catch (Exception e) {
            clear();
            throw e;
        }
    }

    /** Stops the current server. */
    private void stop() throws Exception {
        try {
            stop(server);
        } finally {
            clear();
        }
    }

    /** Gets an available port. */
    private static int getAvailablePort() throws IOException {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        }
    }

    /** Clears the fields for the server. */
    private void clear() {
        server = null;
        port = 0;
        rootUrl = "";
    }

    /** Checks that the server has started. */
    private void checkServerStarted() {
        if (server == null) {
            throw new IllegalStateException("server has not started");
        }
    }
}
