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
import org.example.age.api.client.AvsApi;
import org.example.age.module.client.testing.TestDependenciesModule;
import org.example.age.service.module.client.testing.SiteClientTestTemplate;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class SiteClientTest extends SiteClientTestTemplate {

    @RegisterExtension
    private static final DropwizardAppExtension<Configuration> app =
            new DropwizardAppExtension<>(TestApp.class, null, ConfigOverride.randomPorts());

    private static AvsApi avsClient;

    @BeforeAll
    public static void createClients() {
        TestComponent component = TestComponent.create(app.getLocalPort());
        avsClient = component.avsClient();
    }

    @Override
    protected AvsApi avsClient() {
        return avsClient;
    }

    /** Test application that runs the test service. */
    public static final class TestApp extends Application<Configuration> {

        @Override
        public void run(Configuration config, Environment env) {
            env.jersey().register(new StubAvsService());
        }
    }

    /** Dagger component for the clients. */
    @Component(modules = {SiteClientModule.class, TestDependenciesModule.class})
    @Singleton
    interface TestComponent {

        static TestComponent create(int port) {
            return DaggerSiteClientTest_TestComponent.factory().create(port);
        }

        @Named("client")
        AvsApi avsClient();

        @Component.Factory
        interface Factory {

            TestComponent create(@BindsInstance @Named("port") int port);
        }
    }
}
