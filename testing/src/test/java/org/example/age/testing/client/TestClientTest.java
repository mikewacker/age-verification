package org.example.age.testing.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.dropwizard.core.Application;
import io.dropwizard.core.Configuration;
import io.dropwizard.core.setup.Environment;
import io.dropwizard.testing.ConfigOverride;
import io.dropwizard.testing.junit5.DropwizardAppExtension;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.List;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import redis.clients.jedis.RedisClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;

public final class TestClientTest {

    @RegisterExtension
    private static final DropwizardAppExtension<Configuration> app =
            new DropwizardAppExtension<>(TestApp.class, null, ConfigOverride.randomPorts());

    @Test
    public void http() throws IOException {
        OkHttpClient client = TestClient.http();
        String url = String.format("%s/test", TestClient.localhostUrl(app.getLocalPort()));
        Request request =
                new Request.Builder().url(url).header("Test-Header", "value").build();
        try (okhttp3.Response response = client.newCall(request).execute()) {
            assertThat(response.isSuccessful()).isTrue();
            assertThat(response.body().string()).isEqualTo("[\"value\"]");
        }
    }

    @Test
    public void api() throws IOException {
        URL url = TestClient.localhostUrl(app.getLocalPort());
        TestApi client =
                TestClient.api(url, requestBuilder -> requestBuilder.header("Test-Header", "value"), TestApi.class);
        Response<List<String>> response = client.test().execute();
        assertThat(response.isSuccessful()).isTrue();
        assertThat(response.body()).containsExactly("value");
    }

    @Test
    public void docker() {
        URI uri = TestClient.dockerUri("redis", 6379);
        try (RedisClient client = RedisClient.create(uri)) {
            client.set("key", "value");
            String value = client.get("key");
            assertThat(value).isEqualTo("value");
        }
    }

    @Test
    public void httpSingleton() {
        OkHttpClient client = TestClient.http();
        assertThat(client).isSameAs(TestClient.http());
    }

    @Test
    public void error_Docker_PortMappingNotFound() {
        assertThatThrownBy(() -> TestClient.dockerUrl("dynamodb", 8000))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Docker port mapping not found for dynamodb:8000");
    }

    /** Client for a test API that responds with the value of the test header. */
    interface TestApi {

        @GET("test")
        Call<List<String>> test();
    }

    /** Test endpoint that corresponds to {@link TestApi}. */
    @Path("test")
    @Produces(MediaType.APPLICATION_JSON)
    public static final class TestEndpoint {

        @jakarta.ws.rs.GET
        public List<String> test(@HeaderParam("Test-Header") String value) {
            return List.of(value);
        }
    }

    /** Test application for {@link TestEndpoint}. */
    public static final class TestApp extends Application<Configuration> {

        @Override
        public void run(Configuration config, Environment env) {
            env.jersey().register(new TestEndpoint());
        }
    }
}
