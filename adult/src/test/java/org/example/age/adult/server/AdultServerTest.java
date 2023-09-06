package org.example.age.adult.server;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.example.age.testing.TestClient;
import org.example.age.testing.TestServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class AdultServerTest {

    @RegisterExtension
    private static final TestServer server = TestServer.create((int port) -> AdultServer.create("localhost", port));

    @Test
    public void exchange() throws IOException {
        OkHttpClient client = TestClient.getInstance();
        Request request = new Request.Builder().url(server.getRootUrl()).build();
        Response response = client.newCall(request).execute();
        assertThat(response.code()).isEqualTo(303);
    }
}
