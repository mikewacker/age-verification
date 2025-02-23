package org.example.age.testing;

import java.io.IOException;
import java.net.ServerSocket;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import redis.clients.jedis.JedisPooled;
import redis.embedded.RedisServer;

/** Redis client and server for testing. */
public final class RedisExtension implements BeforeAllCallback, AfterAllCallback {

    private int port;

    private RedisServer server;
    private JedisPooled client;

    /** Creates a client and a server that is bound to any available port. */
    public RedisExtension() {
        this(0);
    }

    /** Creates a client and a server that is bound to the provided port. */
    public RedisExtension(int port) {
        this.port = port;
    }

    /** Gets the port of the server. */
    public int port() {
        checkIsInitialized();
        return port;
    }

    /** Gets the server. */
    public RedisServer server() {
        checkIsInitialized();
        return server;
    }

    /** Gets the client. */
    public JedisPooled client() {
        checkIsInitialized();
        return client;
    }

    @Override
    public void beforeAll(ExtensionContext context) throws IOException {
        port = (port != 0) ? port : findAvailablePort();
        server = new RedisServer(port);
        server.start();
        client = new JedisPooled("localhost", port);
    }

    @Override
    public void afterAll(ExtensionContext context) throws IOException {
        if (server != null) {
            server.stop();
        }
        if (client != null) {
            client.close();
        }
    }

    /** Finds any available port. */
    private static int findAvailablePort() throws IOException {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        }
    }

    /** Checks that Redis is initialized. */
    private void checkIsInitialized() {
        if (client == null) {
            throw new IllegalStateException("Redis not initialized (missing @RegisterExtension?)");
        }
    }
}
