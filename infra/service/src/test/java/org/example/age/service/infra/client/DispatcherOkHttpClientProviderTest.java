package org.example.age.service.infra.client;

import static io.github.mikewacker.drift.testing.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.mikewacker.drift.api.HttpOptional;
import java.io.IOException;
import okhttp3.mockwebserver.MockResponse;
import org.example.age.client.infra.JsonApiClient;
import org.example.age.testing.server.TestServer;
import org.example.age.testing.server.mock.MockServer;
import org.example.age.testing.server.undertow.TestUndertowServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class DispatcherOkHttpClientProviderTest {

    @RegisterExtension
    private static final TestServer<?> frontendServer =
            TestUndertowServer.register("frontend", GreetingService::createHandler);

    @RegisterExtension
    private static final MockServer backendServer = MockServer.register("backend");

    @Test
    public void backendRequest() throws IOException {
        backendServer.enqueue(new MockResponse().setBody("\"world\""));
        HttpOptional<String> maybeGreeting = JsonApiClient.requestBuilder(new TypeReference<String>() {})
                .get(frontendServer.rootUrl())
                .build()
                .execute();
        assertThat(maybeGreeting).hasValue("Hello, world!");
    }
}
