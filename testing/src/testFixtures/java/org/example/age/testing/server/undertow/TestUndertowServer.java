package org.example.age.testing.server.undertow;

import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.PathHandler;
import org.example.age.testing.server.TestServer;
import org.example.age.testing.server.TestServerFactory;

/**
 * Test server backed by an {@link Undertow} server, which can be built from an {@link HttpHandler}.
 *
 * <p>A single server is started for all tests.</p>
 */
public final class TestUndertowServer extends TestServer<Undertow> {

    /** Creates and registers a {@link TestUndertowServer}. */
    public static TestUndertowServer register(String name, TestServerFactory<Undertow> serverFactory) {
        TestUndertowServer server = new TestUndertowServer(serverFactory);
        TestServer.register(name, server);
        return server;
    }

    /** Creates and registers a {@link TestUndertowServer} that is built from a root {@link HttpHandler}. */
    public static TestUndertowServer register(String name, TestHandlerFactory rootHandlerFactory) {
        TestServerFactory<Undertow> serverFactory = new RootHandlerAdapter(rootHandlerFactory);
        return register(name, serverFactory);
    }

    /** Creates and registers a {@link TestUndertowServer} that is built from a prefix {@link HttpHandler}. */
    public static TestUndertowServer register(String name, TestHandlerFactory prefixHandlerFactory, String prefixPath) {
        TestHandlerFactory rootHandlerFactory = new PrefixHandlerAdapter(prefixHandlerFactory, prefixPath);
        return register(name, rootHandlerFactory);
    }

    @Override
    protected void start(Undertow server, int port) {
        server.start();
    }

    @Override
    protected void stop(Undertow server) {
        server.stop();
    }

    private TestUndertowServer(TestServerFactory<Undertow> serverFactory) {
        super(serverFactory, false);
    }

    /** {@link TestServerFactory} that is built from a {@link TestHandlerFactory}. */
    private record RootHandlerAdapter(TestHandlerFactory rootHandlerFactory) implements TestServerFactory<Undertow> {

        @Override
        public Undertow create(int port) {
            HttpHandler rootHandler = rootHandlerFactory.create();
            return Undertow.builder()
                    .addHttpListener(port, "localhost")
                    .setHandler(rootHandler)
                    .build();
        }
    }

    /** Root {@link TestHandlerFactory} that is built from a prefix {@link TestHandlerFactory}. */
    private record PrefixHandlerAdapter(TestHandlerFactory prefixHandlerFactory, String prefixPath)
            implements TestHandlerFactory {

        @Override
        public HttpHandler create() {
            HttpHandler prefixHandler = prefixHandlerFactory.create();
            PathHandler rootHandler = new PathHandler();
            rootHandler.addPrefixPath(prefixPath, prefixHandler);
            return rootHandler;
        }
    }
}
