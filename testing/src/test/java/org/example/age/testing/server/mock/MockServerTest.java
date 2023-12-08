package org.example.age.testing.server.mock;

import static org.example.age.testing.api.HttpOptionalAssert.assertThat;

import java.io.IOException;
import okhttp3.mockwebserver.MockResponse;
import org.example.age.api.HttpOptional;
import org.example.age.testing.client.TestClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class MockServerTest {

    @RegisterExtension
    public static final MockServer server = MockServer.register("test");

    @Test
    public void exchange() throws IOException {
        server.enqueue(new MockResponse().setHeader("Content-Type", "text/html").setBody("<p>test</p>"));
        HttpOptional<String> maybeHtml = TestClient.getHtml(server.rootUrl());
        assertThat(maybeHtml).hasValue("<p>test</p>");
    }
}
