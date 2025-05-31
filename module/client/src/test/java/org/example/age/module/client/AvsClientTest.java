package org.example.age.module.client;

import dagger.BindsInstance;
import dagger.Component;
import io.dropwizard.core.Application;
import io.dropwizard.core.Configuration;
import io.dropwizard.core.setup.Environment;
import io.dropwizard.testing.ConfigOverride;
import io.dropwizard.testing.junit5.DropwizardAppExtension;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import org.example.age.module.client.testing.TestDependenciesModule;
import org.example.age.service.module.client.SiteClientRepository;
import org.example.age.service.module.client.testing.AvsClientTestTemplate;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class AvsClientTest extends AvsClientTestTemplate {

    @RegisterExtension
    private static final DropwizardAppExtension<Configuration> app =
            new DropwizardAppExtension<>(TestApp.class, null, ConfigOverride.randomPorts());

    private static SiteClientRepository siteClients;

    @BeforeAll
    public static void createClients() {
        TestComponent component = TestComponent.create(app.getLocalPort());
        siteClients = component.siteClients();
    }

    @Override
    protected SiteClientRepository siteClients() {
        return siteClients;
    }

    /** Test application that runs the test service. */
    public static final class TestApp extends Application<Configuration> {

        @Override
        public void run(Configuration config, Environment env) {
            env.jersey().register(new StubSiteService());
        }
    }

    /** Dagger component for the clients. */
    @Component(modules = {AvsClientModule.class, TestDependenciesModule.class})
    @Singleton
    interface TestComponent {

        static TestComponent create(int port) {
            return DaggerAvsClientTest_TestComponent.factory().create(port);
        }

        SiteClientRepository siteClients();

        @Component.Factory
        interface Factory {

            TestComponent create(@BindsInstance @Named("port") int port);
        }
    }
}
