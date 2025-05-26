package org.example.age.testing;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URL;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.Test;

public final class TestClientTest {

    @Test
    public void get() {
        OkHttpClient client = TestClient.get();
        assertThat(client).isNotNull();
        assertThat(client).isSameAs(TestClient.get());
    }

    @Test
    public void createLocalhostUrl() {
        URL url = TestClient.createLocalhostUrl(8080);
        assertThat(url.toString()).isEqualTo("http://localhost:8080");
    }
}
