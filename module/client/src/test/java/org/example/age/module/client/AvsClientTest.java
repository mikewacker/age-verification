package org.example.age.module.client;

import dagger.Component;
import io.dropwizard.core.Application;
import io.dropwizard.core.Configuration;
import io.dropwizard.core.setup.Environment;
import io.dropwizard.testing.junit5.DropwizardAppExtension;
import jakarta.inject.Singleton;
import java.util.function.Supplier;
import org.example.age.module.client.testing.TestDependenciesModule;
import org.example.age.service.module.client.SiteClientRepository;
import org.example.age.service.module.client.testing.AvsClientTestTemplate;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class AvsClientTest extends AvsClientTestTemplate {

    private static final SiteClientRepository siteClients = TestComponent.create();

    @RegisterExtension
    private static final DropwizardAppExtension<Configuration> app = new DropwizardAppExtension<>(TestApp.class);

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

    /** Dagger component for {@link SiteClientRepository}. */
    @Component(modules = {AvsClientModule.class, TestDependenciesModule.class})
    @Singleton
    interface TestComponent extends Supplier<SiteClientRepository> {

        static SiteClientRepository create() {
            return DaggerAvsClientTest_TestComponent.create().get();
        }
    }
}
