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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class RequestTest {

    @RegisterExtension
    private static final DropwizardAppExtension<Configuration> app = new DropwizardAppExtension<>(TestApp.class);

    @Test
    public void httpHeaders() throws IOException {
        Request request = new Request.Builder()
                .url(TestClient.localhostUrl(8080))
                .header("Custom-Header", "value")
                .build();
        try (Response response = TestClient.http().newCall(request).execute()) {
            assertThat(response.isSuccessful()).isTrue();
            assertThat(response.body().string()).isEqualTo("value");
        }
    }

    /** Test service that responds with the value of a custom header. */
    @Path("")
    @Produces(MediaType.TEXT_PLAIN)
    public record TestService(RequestContext requestContext) {

        @GET
        public String method() {
            return requestContext.httpHeaders().getHeaderString("Custom-Header");
        }
    }

    /** Test application that runs the test service. */
    public static final class TestApp extends Application<Configuration> {

        @Override
        public void run(Configuration config, Environment env) {
            RequestContext request = TestComponent.create(env);
            TestService service = new TestService(request);
            env.jersey().register(service);
        }
    }

    @Component(modules = RequestModule.class)
    @Singleton
    interface TestComponent extends Supplier<RequestContext> {

        static RequestContext create(Environment env) {
            return DaggerRequestTest_TestComponent.factory().create(env).get();
        }

        @Component.Factory
        interface Factory {

            TestComponent create(@BindsInstance Environment env);
        }
    }
}
