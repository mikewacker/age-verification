package org.example.age.client.infra;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public final class JsonApiRequestTest {

    private static OkHttpClient client;

    private MockWebServer mockServer;
    private String mockServerUrl;

    @BeforeAll
    public static void createClient() {
        client = new OkHttpClient();
    }

    @BeforeEach
    public void startMockServer() throws IOException {
        mockServer = new MockWebServer();
        mockServer.start();
        mockServerUrl = mockServer.url("").toString();
    }

    @AfterEach
    public void stopMockServer() throws IOException {
        mockServer.shutdown();
    }

    @Test
    public void execute_Get() throws Exception {
        mockServer.enqueue(new MockResponse());
        Response response = JsonApiRequest.builder(client).get(mockServerUrl).execute();
        assertThat(response.isSuccessful()).isTrue();

        RecordedRequest recordedRequest = mockServer.takeRequest();
        assertThat(recordedRequest.getMethod()).isEqualTo("GET");
        assertThat(recordedRequest.getPath()).isEqualTo("/");
    }

    @Test
    public void execute_GetWithHeader() throws Exception {
        mockServer.enqueue(new MockResponse());
        Response response = JsonApiRequest.builder(client)
                .get(mockServerUrl)
                .headers(Map.of("User-Agent", "agent"))
                .execute();
        assertThat(response.isSuccessful()).isTrue();

        RecordedRequest recordedRequest = mockServer.takeRequest();
        assertThat(recordedRequest.getMethod()).isEqualTo("GET");
        assertThat(recordedRequest.getHeader("User-Agent")).isEqualTo("agent");
    }

    @Test
    public void execute_Post() throws Exception {
        mockServer.enqueue(new MockResponse());
        Response response = JsonApiRequest.builder(client).post(mockServerUrl).execute();
        assertThat(response.isSuccessful()).isTrue();

        RecordedRequest recordedRequest = mockServer.takeRequest();
        assertThat(recordedRequest.getMethod()).isEqualTo("POST");
    }

    @Test
    public void execute_PostWithBody() throws Exception {
        mockServer.enqueue(new MockResponse());
        Response response =
                JsonApiRequest.builder(client).post(mockServerUrl).body("test").execute();
        assertThat(response.isSuccessful()).isTrue();

        RecordedRequest recordedRequest = mockServer.takeRequest();
        assertThat(recordedRequest.getMethod()).isEqualTo("POST");
        assertThat(recordedRequest.getHeader("Content-Type")).isEqualTo("application/json");
        assertThat(recordedRequest.getBody().readString(StandardCharsets.UTF_8)).isEqualTo("\"test\"");
    }

    @Test
    public void enqueue() throws Exception {
        CountDownLatch requestCompleted = new CountDownLatch(1);
        AtomicBoolean requestSucceeded = new AtomicBoolean(false);
        Callback callback = new Callback() {

            @Override
            public void onResponse(Call call, Response response) {
                requestSucceeded.set(response.isSuccessful());
                requestCompleted.countDown();
            }

            @Override
            public void onFailure(Call call, IOException e) {
                requestCompleted.countDown();
            }
        };

        mockServer.enqueue(new MockResponse());
        JsonApiRequest.builder(client).get(mockServerUrl).enqueue(callback);
        assertThat(requestCompleted.await(10, TimeUnit.MILLISECONDS)).isTrue();
        assertThat(requestSucceeded).isTrue();
    }

    @Test
    public void error_MethodAndUrlNotSet() {
        JsonApiRequest.Builder requestBuilder = JsonApiRequest.builder(client);
        assertThatThrownBy(requestBuilder::execute)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("method and URL are not set");
    }

    @Test
    public void error_BodySetForGetRequest() {
        JsonApiRequest.Builder requestBuilder =
                JsonApiRequest.builder(client).get(mockServerUrl).body("\"test\"".getBytes(StandardCharsets.UTF_8));
        assertThatThrownBy(requestBuilder::execute)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("body is set for GET request");
    }
}
