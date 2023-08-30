package org.example.age.testing;

import static org.assertj.core.api.Assertions.assertThat;

import okhttp3.OkHttpClient;
import org.junit.jupiter.api.Test;

public final class TestClientTest {

    @Test
    public void getInstance() {
        OkHttpClient client1 = TestClient.getInstance();
        OkHttpClient client2 = TestClient.getInstance();
        assertThat(client1).isSameAs(client2);
    }
}
