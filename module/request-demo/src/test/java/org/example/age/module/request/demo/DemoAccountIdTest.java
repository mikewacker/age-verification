package org.example.age.module.request.demo;

import static org.assertj.core.api.Assertions.assertThat;

import dagger.Component;
import io.dropwizard.core.Application;
import io.dropwizard.core.Configuration;
import io.dropwizard.core.setup.Environment;
import io.dropwizard.testing.ConfigOverride;
import io.dropwizard.testing.junit5.DropwizardAppExtension;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.io.IOException;
import okhttp3.Request;
import okhttp3.Response;
import org.example.age.common.testing.TestClient;
import org.example.age.service.module.request.AccountIdContext;
import org.example.age.service.module.request.RequestContextProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class DemoAccountIdTest {

    @RegisterExtension
    private static final DropwizardAppExtension<Configuration> app =
            new DropwizardAppExtension<>(TestApp.class, null, ConfigOverride.randomPorts());

    @Test
    public void accountId() throws IOException {
        String url = String.format("http://localhost:%d/test", app.getLocalPort());
        Request request =
                new Request.Builder().url(url).header("Account-Id", "username").build();
        try (Response response = TestClient.getHttp().newCall(request).execute()) {
            assertThat(response.isSuccessful()).isTrue();
            assertThat(response.body().string()).isEqualTo("username");
        }
    }

    @Test
    public void noAccountId() throws IOException {
        String url = String.format("http://localhost:%d/test", app.getLocalPort());
        Request request = new Request.Builder().url(url).build();
        try (Response response = TestClient.getHttp().newCall(request).execute()) {
            assertThat(response.code()).isEqualTo(401);
        }
    }

    /** Test service that responds with the account ID. */
    @Singleton
    @Path("test")
    @Produces(MediaType.TEXT_PLAIN)
    public static final class TestService {

        private final AccountIdContext accountIdContext;

        @Inject
        public TestService(AccountIdContext accountIdContext) {
            this.accountIdContext = accountIdContext;
        }

        @GET
        public String accountId() {
            return accountIdContext.getForRequest();
        }
    }

    /** Test application that runs {@link TestService}. */
    public static final class TestApp extends Application<Configuration> {

        @Override
        public void run(Configuration config, Environment env) {
            TestComponent component = TestComponent.create();
            env.jersey().register(component.service());
            env.jersey().register(component.requestContextProvider());
        }
    }

    /** Dagger component for the application. */
    @Component(modules = DemoAccountIdModule.class)
    @Singleton
    interface TestComponent {

        static TestComponent create() {
            return DaggerDemoAccountIdTest_TestComponent.create();
        }

        TestService service();

        RequestContextProvider requestContextProvider();
    }
}
