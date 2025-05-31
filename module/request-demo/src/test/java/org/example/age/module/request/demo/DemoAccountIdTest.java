package org.example.age.module.request.demo;

import dagger.BindsInstance;
import dagger.Component;
import io.dropwizard.core.Application;
import io.dropwizard.core.Configuration;
import io.dropwizard.core.setup.Environment;
import io.dropwizard.testing.ConfigOverride;
import io.dropwizard.testing.junit5.DropwizardAppExtension;
import jakarta.inject.Singleton;
import okhttp3.Request;
import org.example.age.module.common.testing.TestLiteEnvModule;
import org.example.age.module.common.testing.TestProviderRegistrar;
import org.example.age.service.module.request.AccountIdContext;
import org.example.age.service.module.request.testing.AccountIdTestTemplate;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class DemoAccountIdTest extends AccountIdTestTemplate {

    @RegisterExtension
    private static final DropwizardAppExtension<Configuration> app =
            new DropwizardAppExtension<>(TestApp.class, null, ConfigOverride.randomPorts());

    @Override
    protected int port() {
        return app.getLocalPort();
    }

    @Override
    protected void setAccountId(Request.Builder requestBuilder, String accountId) {
        requestBuilder.header("Account-Id", accountId);
    }

    /** Test application that runs the test service. */
    public static final class TestApp extends Application<Configuration> {

        @Override
        public void run(Configuration config, Environment env) {
            TestComponent component =
                    TestComponent.create(provider -> env.jersey().register(provider));
            TestService service = new TestService(component.accountIdContext());
            env.jersey().register(service);
        }
    }

    /** Dagger component for the request. */
    @Component(modules = {DemoAccountIdModule.class, TestLiteEnvModule.class})
    @Singleton
    interface TestComponent {

        static TestComponent create(TestProviderRegistrar providerRegistrar) {
            return DaggerDemoAccountIdTest_TestComponent.factory().create(providerRegistrar);
        }

        AccountIdContext accountIdContext();

        @Component.Factory
        interface Factory {

            TestComponent create(@BindsInstance TestProviderRegistrar providerRegistrar);
        }
    }
}
