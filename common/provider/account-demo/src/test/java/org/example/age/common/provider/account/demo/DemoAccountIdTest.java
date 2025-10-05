package org.example.age.common.provider.account.demo;

import dagger.BindsInstance;
import dagger.Component;
import io.dropwizard.core.Application;
import io.dropwizard.core.Configuration;
import io.dropwizard.core.setup.Environment;
import io.dropwizard.testing.ConfigOverride;
import io.dropwizard.testing.junit5.DropwizardAppExtension;
import jakarta.inject.Singleton;
import java.util.function.Supplier;
import okhttp3.Request;
import org.example.age.common.spi.AccountIdContext;
import org.example.age.testing.common.spi.AccountIdTestTemplate;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class DemoAccountIdTest extends AccountIdTestTemplate {

    @RegisterExtension
    private static final DropwizardAppExtension<Configuration> app =
            new DropwizardAppExtension<>(TestApp.class, null, ConfigOverride.randomPorts());

    @Override
    protected void setAccountId(Request.Builder requestBuilder, String accountId) {
        requestBuilder.header("Account-Id", accountId);
    }

    @Override
    protected int port() {
        return app.getLocalPort();
    }

    /** Test application for {@link TestEndpoint}. */
    public static final class TestApp extends Application<Configuration> {

        @Override
        public void run(Configuration config, Environment env) {
            AccountIdContext accountIdContext = TestComponent.create(env);
            TestEndpoint endpoint = new TestEndpoint(accountIdContext);
            env.jersey().register(endpoint);
        }
    }

    /** Dagger component for {@link AccountIdContext}. */
    @Component(modules = DemoAccountIdModule.class)
    @Singleton
    interface TestComponent extends Supplier<AccountIdContext> {

        static AccountIdContext create(Environment env) {
            return DaggerDemoAccountIdTest_TestComponent.factory().create(env).get();
        }

        @Component.Factory
        interface Factory {

            TestComponent create(@BindsInstance Environment env);
        }
    }
}
