package org.example.age.module.client.testing;

import java.io.IOException;
import java.net.ServerSocket;

/** Available port that is lazily found. */
public final class LazyPort {

    private int port = 0;

    /** Gets the port. */
    public int get() {
        if (port == 0) {
            try (ServerSocket socket = new ServerSocket(port)) {
                port = socket.getLocalPort();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return port;
    }
}
