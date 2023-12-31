package org.example.age.client.infra;

import static io.github.mikewacker.drift.testing.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.mikewacker.drift.api.HttpOptional;
import io.github.mikewacker.drift.json.JsonSerializationException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;
import org.example.age.testing.server.mock.MockServer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class OkHttpJsonApiClientTest {

    @RegisterExtension
    private static final MockServer mockServer = MockServer.register("test");

    private static TestClient client;

    @BeforeAll
    public static void createClient() {
        client = TestClient.create();
    }

    @Test
    public void get_StatusCodeResponse() throws IOException {
        mockServer.enqueue(new MockResponse());
        int statusCode =
                client.requestBuilder().get(mockServer.rootUrl()).build().execute();
        assertThat(statusCode).isEqualTo(200);
    }

    @Test
    public void get_JsonValueResponse_Ok() throws IOException {
        mockServer.enqueue(
                new MockResponse().setHeader("Content-Type", "application/json").setBody("\"test\""));
        HttpOptional<String> maybeText = client.requestBuilder(new TypeReference<String>() {})
                .get(mockServer.rootUrl())
                .build()
                .execute();
        assertThat(maybeText).hasValue("test");
    }

    @Test
    public void get_JsonValueResponse_ErrorCode() throws IOException {
        mockServer.enqueue(new MockResponse().setResponseCode(403));
        HttpOptional<String> maybeText = client.requestBuilder(new TypeReference<String>() {})
                .get(mockServer.rootUrl())
                .build()
                .execute();
        assertThat(maybeText).isEmptyWithErrorCode(403);
    }

    @Test
    public void get_RequestHeaders() throws Exception {
        mockServer.enqueue(new MockResponse());
        int statusCode = client.requestBuilder()
                .get(mockServer.rootUrl())
                .headers(Map.of("User-Agent", "agent"))
                .build()
                .execute();
        assertThat(statusCode).isEqualTo(200);

        RecordedRequest recordedRequest = mockServer.get().takeRequest();
        assertThat(recordedRequest.getMethod()).isEqualTo("GET");
        assertThat(recordedRequest.getHeader("User-Agent")).isEqualTo("agent");
    }

    @Test
    public void post_NoRequestBody() throws Exception {
        mockServer.enqueue(new MockResponse());
        int statusCode =
                client.requestBuilder().post(mockServer.rootUrl()).build().execute();
        assertThat(statusCode).isEqualTo(200);

        RecordedRequest recordedRequest = mockServer.get().takeRequest();
        assertThat(recordedRequest.getMethod()).isEqualTo("POST");
    }

    @Test
    public void post_RequestBody() throws Exception {
        mockServer.enqueue(new MockResponse());
        int statusCode = client.requestBuilder()
                .post(mockServer.rootUrl())
                .body("test")
                .build()
                .execute();
        assertThat(statusCode).isEqualTo(200);

        RecordedRequest recordedRequest = mockServer.get().takeRequest();
        assertThat(recordedRequest.getMethod()).isEqualTo("POST");
        assertThat(recordedRequest.getHeader("Content-Type")).isEqualTo("application/json");
        assertThat(recordedRequest.getBody().readString(StandardCharsets.UTF_8)).isEqualTo("\"test\"");
    }

    @Test
    public void error_MissingResponseContentType() {
        mockServer.enqueue(new MockResponse());
        TestClient.ExecuteStage<HttpOptional<String>> executor = client.requestBuilder(new TypeReference<String>() {})
                .get(mockServer.rootUrl())
                .build();
        error_InvalidResponse(executor, IllegalArgumentException.class, "response Content-Type is missing");
    }

    @Test
    public void error_InvalidResponseContentType() {
        mockServer.enqueue(new MockResponse().setHeader("Content-Type", "abc"));
        TestClient.ExecuteStage<HttpOptional<String>> executor = client.requestBuilder(new TypeReference<String>() {})
                .get(mockServer.rootUrl())
                .build();
        error_InvalidResponse(executor, IllegalArgumentException.class, "failed to parse response Content-Type: abc");
    }

    @Test
    public void error_WrongResponseContentType() {
        mockServer.enqueue(new MockResponse().setHeader("Content-Type", "text/html"));
        TestClient.ExecuteStage<HttpOptional<String>> executor = client.requestBuilder(new TypeReference<String>() {})
                .get(mockServer.rootUrl())
                .build();
        error_InvalidResponse(
                executor, IllegalArgumentException.class, "response Content-Type is not application/json: text/html");
    }

    @Test
    public void error_DeserializeResponseBodyFailed() {
        mockServer.enqueue(
                new MockResponse().setHeader("Content-Type", "application/json").setBody("{"));
        TestClient.ExecuteStage<HttpOptional<String>> executor = client.requestBuilder(new TypeReference<String>() {})
                .get(mockServer.rootUrl())
                .build();
        error_InvalidResponse(executor, JsonSerializationException.class, "deserialization failed");
    }

    private void error_InvalidResponse(
            TestClient.ExecuteStage<?> executor, Class<?> expectedExceptionClass, String expectedMessage) {
        assertThatThrownBy(() -> executor.execute())
                .isInstanceOf(expectedExceptionClass)
                .hasMessage(expectedMessage);
    }

    @Test
    public void error_StageCompleted() {
        ApiClient.UrlStageRequestBuilder<TestClient.ExecuteStage<Integer>> requestBuilder = client.requestBuilder();
        requestBuilder.get(mockServer.rootUrl());
        assertThatThrownBy(() -> requestBuilder.get(mockServer.rootUrl()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("stage already completed");
    }

    /** Test JSON API client. */
    private static final class TestClient extends OkHttpJsonApiClient {

        private static final OkHttpClient client = new OkHttpClient();

        public static TestClient create() {
            return new TestClient();
        }

        public UrlStageRequestBuilder<ExecuteStage<Integer>> requestBuilder() {
            return requestBuilder(ExecuteStageImpl::new);
        }

        public <V> UrlStageRequestBuilder<ExecuteStage<HttpOptional<V>>> requestBuilder(
                TypeReference<V> responseValueTypeRef) {
            return requestBuilder(ExecuteStageImpl::new, responseValueTypeRef);
        }

        private TestClient() {}

        /** Post-build stage that can synchronously execute the request. */
        public interface ExecuteStage<V> {

            V execute() throws IOException;
        }

        /** Internal {@link ExecuteStage} implementation. */
        private record ExecuteStageImpl<V>(Request request, ResponseConverter<V> responseConverter)
                implements ExecuteStage<V> {

            @Override
            public V execute() throws IOException {
                Call call = client.newCall(request);
                Response response = call.execute();
                return responseConverter.convert(response);
            }
        }
    }
}
