package org.example.age.infra.client;

import static org.assertj.core.api.Assertions.assertThat;

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
        Response response = JsonApiRequest.builder(client).url(mockServerUrl).get().execute();
        assertThat(response.isSuccessful()).isTrue();
        RecordedRequest recordedRequest = mockServer.takeRequest();
        assertThat(recordedRequest.getPath()).isEqualTo("/");
        assertThat(recordedRequest.getMethod()).isEqualTo("GET");
    }

    @Test
    public void execute_GetWithHeader() throws Exception {
        mockServer.enqueue(new MockResponse());
        JsonApiRequest.builder(client)
                .url(mockServerUrl)
                .headers(Map.of("User-Agent", "agent"))
                .get()
                .execute();
        RecordedRequest recordedRequest = mockServer.takeRequest();
        assertThat(recordedRequest.getHeader("User-Agent")).isEqualTo("agent");
        assertThat(recordedRequest.getMethod()).isEqualTo("GET");
    }

    @Test
    public void execute_PostWithoutBody() throws Exception {
        mockServer.enqueue(new MockResponse());
        JsonApiRequest.builder(client).url(mockServerUrl).post().execute();
        RecordedRequest recordedRequest = mockServer.takeRequest();
        assertThat(recordedRequest.getMethod()).isEqualTo("POST");
    }

    @Test
    public void execute_PostWithBody() throws Exception {
        mockServer.enqueue(new MockResponse());
        JsonApiRequest.builder(client)
                .url(mockServerUrl)
                .post("\"test\"".getBytes(StandardCharsets.UTF_8))
                .execute();
        RecordedRequest recordedRequest = mockServer.takeRequest();
        assertThat(recordedRequest.getHeader("Content-Type")).isEqualTo("application/json");
        assertThat(recordedRequest.getMethod()).isEqualTo("POST");
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
        JsonApiRequest.builder(client).url(mockServerUrl).get().enqueue(callback);
        assertThat(requestCompleted.await(10, TimeUnit.MILLISECONDS)).isTrue();
        assertThat(requestSucceeded).isTrue();
    }
}
