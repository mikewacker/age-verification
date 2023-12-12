package org.example.age.testing.server.mock;

import static org.example.age.testing.api.HttpOptionalAssert.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import okhttp3.mockwebserver.MockResponse;
import org.example.age.api.base.HttpOptional;
import org.example.age.testing.client.TestClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class MockServerTest {

    @RegisterExtension
    public static final MockServer server = MockServer.register("test");

    @Test
    public void exchange() throws IOException {
        server.enqueue(
                new MockResponse().setHeader("Content-Type", "application/json").setBody("\"test\""));
        HttpOptional<String> maybeValue = TestClient.requestBuilder(new TypeReference<String>() {})
                .get(server.rootUrl())
                .execute();
        assertThat(maybeValue).hasValue("test");
    }
}
