package org.example.age.module.client;

import dagger.Component;
import io.dropwizard.core.Application;
import io.dropwizard.core.Configuration;
import io.dropwizard.core.setup.Environment;
import io.dropwizard.testing.junit5.DropwizardAppExtension;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import org.example.age.avs.api.client.AvsApi;
import org.example.age.module.client.testing.TestDependenciesModule;
import org.example.age.service.module.client.testing.SiteClientTestTemplate;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class SiteClientTest extends SiteClientTestTemplate {

    private static final AvsApi avsClient = TestComponent.create();

    @RegisterExtension
    private static final DropwizardAppExtension<Configuration> app = new DropwizardAppExtension<>(TestApp.class);

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

    /** Dagger component for the <code>@Named("client") {@link AvsApi}</code>. */
    @Component(modules = {SiteClientModule.class, TestDependenciesModule.class})
    @Singleton
    interface TestComponent {

        static AvsApi create() {
            return DaggerSiteClientTest_TestComponent.create().get();
        }

        @Named("client")
        AvsApi get();
    }
}
