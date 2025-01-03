package org.example.age.testing;

import io.dropwizard.core.Configuration;
import io.dropwizard.core.server.DefaultServerFactory;
import io.dropwizard.jetty.HttpConnectorFactory;

/** Sets the port for a test application. */
public final class TestPort {

    /**
     * Sets the port in the configuration. Use 0 for any available port.
     * <p>
     * Also disables the admin interface, which binds a port.
     */
    public static void set(Configuration config, int port) {
        DefaultServerFactory serverFactory = (DefaultServerFactory) config.getServerFactory();
        HttpConnectorFactory connectorFactory =
                (HttpConnectorFactory) serverFactory.getApplicationConnectors().get(0);
        connectorFactory.setPort(port);
        serverFactory.getAdminConnectors().clear();
    }

    // static class
    private TestPort() {}
}
