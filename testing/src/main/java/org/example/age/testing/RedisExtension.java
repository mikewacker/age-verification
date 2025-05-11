package org.example.age.testing;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import redis.clients.jedis.JedisPooled;
import redis.embedded.RedisServer;

/** Redis client and server for testing. */
public final class RedisExtension extends ClientServerExtension<JedisPooled, RedisServer> {

    /** Creates a client and a server that is bound to any available port. */
    public RedisExtension() {
        super();
    }

    /** Creates a client and a server that is bound to the provided port. */
    public RedisExtension(int port) {
        super(port);
    }

    @Override
    protected RedisServer startServer(int port) throws IOException {
        Path redisDir = createTempDirectory("redis");
        File redisBinary = copyRedisBinaryFromResources(redisDir);
        RedisServer server = new RedisServer(port, redisBinary);
        server.start();
        return server;
    }

    @Override
    protected void stopServer(RedisServer server) throws IOException {
        server.stop();
    }

    @Override
    protected JedisPooled createClient(int port) {
        return new JedisPooled("localhost", port);
    }

    @Override
    protected void closeClient(JedisPooled client) {
        client.close();
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
}
