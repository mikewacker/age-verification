package org.example.age.service.module.request;

import static org.assertj.core.api.Assertions.assertThat;

import dagger.Component;
import io.dropwizard.core.Application;
import io.dropwizard.core.Configuration;
import io.dropwizard.core.setup.Environment;
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
import org.example.age.testing.TestClient;
import org.example.age.testing.TestPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class RequestContextTest {

    @RegisterExtension
    private static final DropwizardAppExtension<Configuration> app = new DropwizardAppExtension<>(TestApp.class);

    @Test
    public void requestContext() throws IOException {
        String url = String.format("http://localhost:%d/test", app.getLocalPort());
        Request request = new Request.Builder().url(url).build();
        try (Response response = TestClient.get().newCall(request).execute()) {
            assertThat(response.isSuccessful()).isTrue();
            assertThat(response.body().string()).isEqualTo("GET");
        }
    }

    /** Test service that responds with the HTTP method. */
    @Singleton
    @Path("/test")
    public static final class TestService {

        private final RequestContextProvider requestContextProvider;

        @Inject
        public TestService(RequestContextProvider requestContextProvider) {
            this.requestContextProvider = requestContextProvider;
        }

        @GET
        @Produces(MediaType.TEXT_PLAIN)
        public String method() {
            return requestContextProvider.get().getMethod();
        }
    }

    /** Test application that runs {@link TestService}. */
    public static final class TestApp extends Application<Configuration> {

        @Override
        public void run(Configuration config, Environment env) {
            TestPort.set(config, 0);
            TestComponent component = TestComponent.create();
            env.jersey().register(component.service());
            env.jersey().register(component.requestContextProvider());
        }
    }

    /** Dagger component for the application. */
    @Component(modules = RequestContextModule.class)
    @Singleton
    interface TestComponent {

        static TestComponent create() {
            return DaggerRequestContextTest_TestComponent.create();
        }

        TestService service();

        RequestContextProvider requestContextProvider();
    }
}
