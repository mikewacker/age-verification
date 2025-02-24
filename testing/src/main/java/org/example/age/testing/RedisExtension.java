package org.example.age.testing;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import redis.clients.jedis.JedisPooled;
import redis.embedded.RedisServer;

/** Redis client and server for testing. */
public final class RedisExtension implements BeforeAllCallback, AfterAllCallback {

    private int port;

    private Path redisDir;
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
        redisDir = Files.createTempDirectory("redis");
        File redisBinary = copyRedisBinaryFromResources(redisDir);
        server = new RedisServer(port, redisBinary);
        server.start();
        client = new JedisPooled("localhost", port);
    }

    @Override
    public void afterAll(ExtensionContext context) throws IOException {
        if (client != null) {
            client.close();
        }
        if (server != null) {
            server.stop();
        }
        if (redisDir != null) {
            try (Stream<Path> paths = Files.walk(redisDir)) {
                paths.sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
            }
        }
    }

    /** Finds any available port. */
    private static int findAvailablePort() throws IOException {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        }
    }

    /** Copies the Redis binary from resources to a file, returning the file. */
    private static File copyRedisBinaryFromResources(Path redisDir) throws IOException {
        Path redisBinaryPath = redisDir.resolve("redis-server");
        ClassLoader classLoader = RedisExtension.class.getClassLoader();
        try (InputStream resourceStream = classLoader.getResourceAsStream("redis-server")) {
            Files.copy(resourceStream, redisBinaryPath);
        }
        File redisBinary = redisBinaryPath.toFile();
        redisBinary.setExecutable(true);
        return redisBinary;
    }

    /** Checks that Redis is initialized. */
    private void checkIsInitialized() {
        if (client == null) {
            throw new IllegalStateException("Redis not initialized (missing @RegisterExtension?)");
        }
    }
}
