package org.example.age.adult.server;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import okhttp3.Response;
import org.example.age.testing.TestClient;
import org.example.age.testing.TestUndertowServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class AdultServerTest {

    @RegisterExtension
    private static final TestUndertowServer server =
            TestUndertowServer.create((int port) -> AdultServer.create("localhost", port));

    @Test
    public void exchange() throws IOException {
        Response response = TestClient.get(server.rootUrl());
        assertThat(response.code()).isEqualTo(303);
    }
}
