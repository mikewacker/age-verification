package org.example.age.app;

import io.dropwizard.core.Application;
import io.dropwizard.core.Configuration;

/**
 * Application that is created with a name and has a convenience method to run it.
 * <p>
 * For the purpose of testing, concrete implementations will still need a no-arg constructor.
 */
public abstract class NamedApp<C extends Configuration> extends Application<C> {

    private final String name;

    @Override
    public final String getName() {
        return name;
    }

    /** Runs the application using the configuration at the provided path. */
    public final void runServer(String appConfigPath) throws Exception {
        run("server", appConfigPath);
    }

    /** Creates a named application. */
    protected NamedApp(String name) {
        this.name = name;
    }
}
