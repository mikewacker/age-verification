package org.example.age.infra.api.exchange;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.example.age.infra.api.exchange.testing.TestAddHandler;
import org.example.age.testing.client.TestClient;
import org.example.age.testing.server.TestUndertowServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class ExchangeUtilsTest {

    @RegisterExtension
    private static final TestUndertowServer server = TestUndertowServer.create(TestAddHandler::create);

    @Test
    public void exchangeWithResponseBody() throws IOException {
        Request request = createRequest("/add?operand=2", "2");
        Response response = TestClient.execute(request);
        assertThat(response.code()).isEqualTo(200);
        assertThat(response.header("Content-Type")).isEqualTo("text/plain");
        assertThat(response.body().string()).isEqualTo("4");
    }

    @Test
    public void exchangeWithOnlyStatusCode() throws IOException {
        Request request = createRequest("/add?operand=400", "18");
        Response response = TestClient.execute(request);
        assertThat(response.code()).isEqualTo(418);
        assertThat(response.body().string()).isEmpty();
    }

    @Test
    public void error_ParamMissing() throws IOException {
        Request request = createRequest("/add", "2");
        Response response = TestClient.execute(request);
        assertThat(response.code()).isEqualTo(400);
    }

    @Test
    public void error_DeserializeParamFailed() throws IOException {
        Request request = createRequest("/add?operand=a", "2");
        Response response = TestClient.execute(request);
        assertThat(response.code()).isEqualTo(400);
    }

    @Test
    public void error_DeserializeBodyFailed() throws IOException {
        Request request = createRequest("/add?operand=2", "a");
        Response response = TestClient.execute(request);
        assertThat(response.code()).isEqualTo(400);
    }

    @Test
    public void error_UncaughtException() throws IOException {
        Request request = createRequest("/add?operand=500", "0");
        Response response = TestClient.execute(request);
        assertThat(response.code()).isEqualTo(500);
    }

    @Test
    public void error_SerializeResponseFailed() throws IOException {
        Request request = createRequest("/add?operand=1000", "337");
        Response response = TestClient.execute(request);
        assertThat(response.code()).isEqualTo(500);
    }

    private static Request createRequest(String path, String body) {
        RequestBody requestBody = RequestBody.create(body, null);
        return new Request.Builder().url(server.url(path)).post(requestBody).build();
    }
}
