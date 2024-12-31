package org.example.age.app;

import io.dropwizard.core.Configuration;
import io.dropwizard.core.setup.Environment;
import org.example.age.service.StubSiteService;

/** Application for a site. */
public final class SiteApp extends NamedApp<Configuration> {

    /** Creates ands runs an application. */
    public static void run(String name, String appConfigPath) throws Exception {
        new SiteApp(name).runServer(appConfigPath);
    }

    /** Creates a named application. */
    public SiteApp(String name) {
        super(name);
    }

    /** Creates an application with the default name. Provided for the purpose of testing. */
    public SiteApp() {}

    @Override
    public void run(Configuration configuration, Environment environment) {
        environment.jersey().register(new StubSiteService());
    }
}
