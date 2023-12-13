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

public final class JsonApiClientTest {

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
        Response response =
                JsonApiClient.requestBuilder(client).get(mockServerUrl).execute();
        assertThat(response.isSuccessful()).isTrue();

        RecordedRequest recordedRequest = mockServer.takeRequest();
        assertThat(recordedRequest.getMethod()).isEqualTo("GET");
        assertThat(recordedRequest.getPath()).isEqualTo("/");
    }

    @Test
    public void execute_GetWithHeader() throws Exception {
        mockServer.enqueue(new MockResponse());
        Response response = JsonApiClient.requestBuilder(client)
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
        Response response =
                JsonApiClient.requestBuilder(client).post(mockServerUrl).execute();
        assertThat(response.isSuccessful()).isTrue();

        RecordedRequest recordedRequest = mockServer.takeRequest();
        assertThat(recordedRequest.getMethod()).isEqualTo("POST");
    }

    @Test
    public void execute_PostWithBody() throws Exception {
        mockServer.enqueue(new MockResponse());
        Response response = JsonApiClient.requestBuilder(client)
                .post(mockServerUrl)
                .body("test")
                .execute();
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
        JsonApiClient.requestBuilder(client).get(mockServerUrl).enqueue(callback);
        assertThat(requestCompleted.await(10, TimeUnit.MILLISECONDS)).isTrue();
        assertThat(requestSucceeded).isTrue();
    }

    @Test
    public void error_StageCompleted() {
        JsonApiClient.UrlStageRequestBuilder requestBuilder = JsonApiClient.requestBuilder(client);
        requestBuilder.get(mockServerUrl);
        assertThatThrownBy(() -> requestBuilder.get(mockServerUrl))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("stage already completed");
    }
}
