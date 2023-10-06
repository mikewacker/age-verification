package org.example.age.testing;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.Test;

public final class TestClientTest {

    @Test
    public void get() throws IOException {
        try (MockWebServer server = new MockWebServer()) {
            server.start();
            server.enqueue(new MockResponse().setBody("Hello, world!"));
            String url = server.url("").toString();
            Response response = TestClient.get(url);
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).isEqualTo("Hello, world!");
        }
    }

    @Test
    public void getInstance() {
        OkHttpClient client1 = TestClient.getInstance();
        OkHttpClient client2 = TestClient.getInstance();
        assertThat(client1).isSameAs(client2);
    }
}
