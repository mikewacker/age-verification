package org.example.age.testing.server.undertow;

import static org.assertj.core.api.Assertions.assertThat;

import io.undertow.server.HttpServerExchange;
import java.io.IOException;
import okhttp3.Request;
import okhttp3.Response;
import org.example.age.testing.server.TestClient;
import org.example.age.testing.server.TestServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class TestUndertowServerTest {

    @RegisterExtension
    private static final TestServer<?> server =
            TestUndertowServer.register("test", "/api/", () -> HttpServerExchange::endExchange);

    @Test
    public void exchange_HandledPath() throws IOException {
        Request request = new Request.Builder().url(server.url("/api/test")).build();
        Response response = TestClient.get().newCall(request).execute();
        assertThat(response.code()).isEqualTo(200);
    }

    @Test
    public void exchange_UnhandledPath() throws IOException {
        Request request = new Request.Builder().url(server.rootUrl()).build();
        Response response = TestClient.get().newCall(request).execute();
        assertThat(response.code()).isEqualTo(404);
    }
}
