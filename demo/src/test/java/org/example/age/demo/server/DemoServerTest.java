package org.example.age.demo.server;

import io.undertow.Undertow;
import org.junit.jupiter.api.Test;

public final class DemoServerTest {

    @Test
    public void startCrackle() {
        Undertow server = DemoSiteServerComponent.createServer("Crackle");
        start(server);
    }

    @Test
    public void startPop() {
        Undertow server = DemoSiteServerComponent.createServer("Pop");
        start(server);
    }

    @Test
    public void startCheckMyAge() {
        Undertow server = DemoAvsServerComponent.createServer("CheckMyAge");
        start(server);
    }

    private static void start(Undertow server) {
        try {
            server.start();
        } finally {
            server.stop();
        }
    }
}
