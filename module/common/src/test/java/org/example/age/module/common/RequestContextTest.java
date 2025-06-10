package org.example.age.module.common;

import static org.assertj.core.api.Assertions.assertThat;

import dagger.BindsInstance;
import dagger.Component;
import io.dropwizard.core.Application;
import io.dropwizard.core.Configuration;
import io.dropwizard.core.setup.Environment;
import io.dropwizard.testing.junit5.DropwizardAppExtension;
import jakarta.inject.Singleton;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.function.Supplier;
import okhttp3.Request;
import okhttp3.Response;
import org.example.age.common.testing.TestClient;
import org.example.age.module.common.testing.TestLiteEnvModule;
import org.example.age.module.common.testing.TestProviderRegistrar;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public class RequestContextTest {

    @RegisterExtension
    private static final DropwizardAppExtension<Configuration> app = new DropwizardAppExtension<>(TestApp.class);

    @Test
    public void requestContext() throws IOException {
        Request request =
                new Request.Builder().url(TestClient.localhostUrl(8080)).build();
        try (Response response = TestClient.http().newCall(request).execute()) {
            assertThat(response.isSuccessful()).isTrue();
            assertThat(response.body().string()).isEqualTo("GET");
        }
    }

    /** Test service that responds with the HTTP method. */
    @Path("")
    @Produces(MediaType.TEXT_PLAIN)
    public record TestService(RequestContextProvider requestContextProvider) {

        @GET
        public String method() {
            return requestContextProvider.get().getMethod();
        }
    }

    /** Test application that runs the test service. */
    public static final class TestApp extends Application<Configuration> {

        @Override
        public void run(Configuration config, Environment env) {
            RequestContextProvider requestContextProvider =
                    TestComponent.create(provider -> env.jersey().register(provider));
            TestService service = new TestService(requestContextProvider);
            env.jersey().register(service);
        }
    }

    /** Dagger component for {@link RequestContextProvider}. */
    @Component(modules = {CommonModule.class, TestLiteEnvModule.class})
    @Singleton
    interface TestComponent extends Supplier<RequestContextProvider> {

        static RequestContextProvider create(TestProviderRegistrar providerRegistrar) {
            return DaggerRequestContextTest_TestComponent.factory()
                    .create(providerRegistrar)
                    .get();
        }

        @Component.Factory
        interface Factory {

            TestComponent create(@BindsInstance TestProviderRegistrar providerRegistrar);
        }
    }
}
