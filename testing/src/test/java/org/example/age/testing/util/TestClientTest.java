package org.example.age.testing.util;

import static org.assertj.core.api.Assertions.assertThat;

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
import java.net.URL;
import java.util.Map;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;

public final class TestClientTest {

    @RegisterExtension
    private static final DropwizardAppExtension<Configuration> app =
            new DropwizardAppExtension<>(TestApp.class, null, ConfigOverride.randomPorts());

    @Test
    public void getHttp() {
        OkHttpClient client = TestClient.http();
        assertThat(client).isNotNull();
        assertThat(client).isSameAs(TestClient.http());
    }

    @Test
    public void createApi() throws IOException {
        TestApi client = TestClient.api(
                app.getLocalPort(), requestBuilder -> requestBuilder.header("Test-Header", "value"), TestApi.class);
        Response<Map<String, String>> response = client.test().execute();
        assertThat(response.isSuccessful()).isTrue();
        assertThat(response.body()).isEqualTo(Map.of("test", "value"));
    }

    @Test
    public void createLocalhostUrl() {
        URL url = TestClient.localhostUrl(8080);
        assertThat(url.toString()).isEqualTo("http://localhost:8080");
    }

    /** Client for a test API that responds with the value of the test header. */
    interface TestApi {

        @GET("test")
        Call<Map<String, String>> test();
    }

    /** Test service for {@link TestApi}. */
    @Path("test")
    @Produces(MediaType.APPLICATION_JSON)
    public static final class TestService {

        @jakarta.ws.rs.GET
        public Map<String, String> test(@HeaderParam("Test-Header") String value) {
            return Map.of("test", value);
        }
    }

    /** Test application that runs {@link TestService}. */
    public static final class TestApp extends Application<Configuration> {

        @Override
        public void run(Configuration config, Environment env) {
            env.jersey().register(new TestService());
        }
    }
}
