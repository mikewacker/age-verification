package org.example.age.common.client.api;

import static org.assertj.core.api.Assertions.assertThat;

import dagger.Component;
import io.dropwizard.core.Application;
import io.dropwizard.core.Configuration;
import io.dropwizard.core.setup.Environment;
import io.dropwizard.testing.ConfigOverride;
import io.dropwizard.testing.junit5.DropwizardAppExtension;
import jakarta.inject.Singleton;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.List;
import java.util.function.Supplier;
import org.example.age.testing.client.TestClient;
import org.example.age.testing.env.TestEnvModule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;

public final class ApiClientTest {

    @RegisterExtension
    private static final DropwizardAppExtension<Configuration> app =
            new DropwizardAppExtension<>(TestApp.class, null, ConfigOverride.randomPorts());

    @Test
    public void useClient() throws IOException {
        TestApi client = TestComponent.create().create(TestClient.localhostUrl(app.getLocalPort()), TestApi.class);
        Response<List<String>> response = client.test().execute();
        assertThat(response.isSuccessful()).isTrue();
        assertThat(response.body()).containsExactly("test");
    }

    /** Test client API. */
    interface TestApi {

        @GET("test")
        Call<List<String>> test();
    }

    /** Test endpoint that corresponds to {@link TestApi}. */
    @Path("test")
    @Produces(MediaType.APPLICATION_JSON)
    public static final class TestEndpoint {

        @jakarta.ws.rs.GET
        public List<String> test() {
            return List.of("test");
        }
    }

    /** Test application for {@link TestEndpoint}. */
    public static final class TestApp extends Application<Configuration> {

        @Override
        public void run(Configuration config, Environment env) {
            env.jersey().register(new TestEndpoint());
        }
    }

    /** Dagger component for {@link ApiClientFactory}. */
    @Component(modules = {ApiClientModule.class, TestEnvModule.class})
    @Singleton
    interface TestComponent extends Supplier<ApiClientFactory> {

        static ApiClientFactory create() {
            return DaggerApiClientTest_TestComponent.create().get();
        }
    }
}
