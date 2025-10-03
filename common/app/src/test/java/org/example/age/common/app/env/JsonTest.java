package org.example.age.common.app.env;

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
import java.util.function.Supplier;
import okhttp3.Request;
import okhttp3.Response;
import org.example.age.common.api.AgeRange;
import org.example.age.common.env.BaseEnvModule;
import org.example.age.common.env.JsonMapper;
import org.example.age.testing.client.TestClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class JsonTest {

    @RegisterExtension
    private static final DropwizardAppExtension<Configuration> app =
            new DropwizardAppExtension<>(TestApp.class, null, ConfigOverride.randomPorts());

    @Test
    public void serializeNonNull() throws IOException {
        Request request = new Request.Builder()
                .url(TestClient.localhostUrl(app.getLocalPort()))
                .build();
        try (Response response = TestClient.http().newCall(request).execute()) {
            assertThat(response.isSuccessful()).isTrue();
            assertThat(response.body().string()).isEqualTo("{\"min\":18}");
        }
    }

    /** Test endpoint that responds with the JSON for an {@link AgeRange} with a null value. */
    @Path("")
    @Produces(MediaType.TEXT_PLAIN)
    public record TestEndpoint(JsonMapper mapper) {

        @GET
        public String nonNull() {
            AgeRange ageRange = AgeRange.builder().min(18).build();
            return mapper.serialize(ageRange);
        }
    }

    /** Test application for {@link TestEndpoint}. */
    public static final class TestApp extends Application<Configuration> {

        @Override
        public void run(Configuration config, Environment env) {
            JsonMapper mapper = TestComponent.create(env);
            TestEndpoint endpoint = new TestEndpoint(mapper);
            env.jersey().register(endpoint);
        }
    }

    /** Dagger component for {@link JsonMapper}.*/
    @Component(modules = {BaseEnvModule.class, DropwizardEnvModule.class})
    @Singleton
    interface TestComponent extends Supplier<JsonMapper> {

        static JsonMapper create(Environment env) {
            return DaggerJsonTest_TestComponent.factory().create(env).get();
        }

        @Component.Factory
        interface Factory {

            TestComponent create(@BindsInstance Environment env);
        }
    }
}
