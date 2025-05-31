package org.example.age.module.common;

import static org.assertj.core.api.Assertions.assertThat;

import dagger.BindsInstance;
import dagger.Component;
import io.dropwizard.core.Application;
import io.dropwizard.core.Configuration;
import io.dropwizard.core.setup.Environment;
import io.dropwizard.testing.ConfigOverride;
import io.dropwizard.testing.junit5.DropwizardAppExtension;
import jakarta.inject.Singleton;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.io.IOException;
import okhttp3.Request;
import okhttp3.Response;
import org.example.age.common.testing.TestClient;
import org.example.age.module.common.testing.TestLiteEnvModule;
import org.example.age.module.common.testing.TestProviderRegistrar;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public class RequestContextTest {

    @RegisterExtension
    private static final DropwizardAppExtension<Configuration> app =
            new DropwizardAppExtension<>(TestApp.class, null, ConfigOverride.randomPorts());

    @Test
    public void requestContext() throws IOException {
        Request request = new Request.Builder()
                .url(TestClient.createLocalhostUrl(app.getLocalPort()))
                .build();
        try (Response response = TestClient.getHttp().newCall(request).execute()) {
            assertThat(response.isSuccessful()).isTrue();
            assertThat(response.body().string()).isEqualTo("GET");
        }
    }

    /** Test service that responds with the HTTP method. */
    @Path("")
    @Produces(MediaType.TEXT_PLAIN)
    public static final class TestService {

        private final RequestContextProvider requestContextProvider;

        public TestService(RequestContextProvider requestContextProvider) {
            this.requestContextProvider = requestContextProvider;
        }

        @GET
        public String method() {
            return requestContextProvider.get().getMethod();
        }
    }

    /** Test application that runs the test service. */
    public static final class TestApp extends Application<Configuration> {

        @Override
        public void run(Configuration config, Environment env) {
            TestComponent component =
                    TestComponent.create(provider -> env.jersey().register(provider));
            TestService service = new TestService(component.requestContextProvider());
            env.jersey().register(service);
        }
    }

    /** Dagger component for the request. */
    @Component(modules = {CommonModule.class, TestLiteEnvModule.class})
    @Singleton
    interface TestComponent {

        static TestComponent create(TestProviderRegistrar providerRegistrar) {
            return DaggerRequestContextTest_TestComponent.factory().create(providerRegistrar);
        }

        RequestContextProvider requestContextProvider();

        @Component.Factory
        interface Factory {

            TestComponent create(@BindsInstance TestProviderRegistrar providerRegistrar);
        }
    }
}
