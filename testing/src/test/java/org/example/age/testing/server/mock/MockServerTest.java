package org.example.age.testing.server.mock;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.mockwebserver.MockResponse;
import org.example.age.testing.server.TestClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class MockServerTest {

    @RegisterExtension
    public static final MockServer server = MockServer.register("test");

    @Test
    public void exchange() throws IOException {
        server.enqueue(new MockResponse());
        Request request = new Request.Builder().url(server.rootUrl()).build();
        Response response = TestClient.get().newCall(request).execute();
        assertThat(response.code()).isEqualTo(200);
    }
}
