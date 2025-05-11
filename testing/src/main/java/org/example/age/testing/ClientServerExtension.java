package org.example.age.testing;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/** Client and server for testing. */
abstract class ClientServerExtension<C, S> implements BeforeAllCallback, AfterAllCallback {

    private int port;

    private final List<Path> tempDirs = new ArrayList<>();
    private S server;
    private C client;

    /** Gets the port. */
    public final int port() {
        checkIsInitialized();
        return port;
    }

    /** Gets the client. */
    public final C client() {
        checkIsInitialized();
        return client;
    }

    /** Gets the server. */
    public final S server() {
        checkIsInitialized();
        return server;
    }

    @Override
    public final void beforeAll(ExtensionContext context) throws Exception {
        port = (port != 0) ? port : findAvailablePort();
        server = startServer(port);
        client = createClient(port);
    }

    @Override
    public final void afterAll(ExtensionContext context) throws Exception {
        if (client != null) {
            closeClient(client);
        }
        if (server != null) {
            stopServer(server);
        }
        for (Path tempDir : tempDirs) {
            Files.walkFileTree(tempDir, new DeleteDirVisitor());
        }
    }

    /** Creates a client and a server that is bound to any available port. */
    protected ClientServerExtension() {
        this(0);
    }

    /** Creates a client and a server that is bound to the provided port. */
    protected ClientServerExtension(int port) {
        this.port = port;
    }

    /** Starts the server. */
    protected abstract S startServer(int port) throws Exception;

    /** Stops the server. */
    protected abstract void stopServer(S server) throws Exception;

    /** Creates the client. */
    protected abstract C createClient(int port) throws Exception;

    /** Closes the client. */
    protected abstract void closeClient(C client) throws Exception;

    /** Creates a temporary directory that will be deleted after all tests have run. */
    protected final Path createTempDirectory(String prefix) throws IOException {
        Path tempDir = Files.createTempDirectory(prefix);
        tempDirs.add(tempDir);
        return tempDir;
    }

    /** Finds any available port. */
    private static int findAvailablePort() throws IOException {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        }
    }

    /** Checks that the client and server have been initialized. */
    private void checkIsInitialized() {
        if (client == null) {
            throw new IllegalStateException("not initialized (missing @RegisterExtension?)");
        }
    }

    /** Recursively deletes a directory. */
    private static final class DeleteDirVisitor extends SimpleFileVisitor<Path> {

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            Files.delete(file);
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException e) throws IOException {
            Files.delete(dir);
            return FileVisitResult.CONTINUE;
        }
    }
}
