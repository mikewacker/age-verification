package org.example.age.app;

import io.dropwizard.core.Configuration;
import io.dropwizard.core.setup.Environment;
import org.example.age.service.AvsService;

/** Application for the age verification service. */
public class AvsApp extends NamedApp<Configuration> {

    /** Creates and runs an application. */
    public static void run(String name, String appConfigPath) throws Exception {
        new AvsApp(name).runServer(appConfigPath);
    }

    /** Creates a named application. */
    public AvsApp(String name) {
        super(name);
    }

    /** Creates an application with the default name. Provided for the purpose of testing. */
    public AvsApp() {}

    @Override
    public void run(Configuration configuration, Environment environment) {
        environment.jersey().register(new AvsService());
    }
}
