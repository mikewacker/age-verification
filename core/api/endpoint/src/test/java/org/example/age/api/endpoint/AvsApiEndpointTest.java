package org.example.age.api.endpoint;

import static io.github.mikewacker.drift.testing.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.mikewacker.drift.api.HttpOptional;
import io.github.mikewacker.drift.client.JsonApiClient;
import io.github.mikewacker.drift.testing.server.TestServer;
import io.github.mikewacker.drift.testing.server.TestUndertowServer;
import java.io.IOException;
import org.example.age.api.def.VerificationState;
import org.example.age.data.certificate.VerificationSession;
import org.example.age.data.crypto.SecureId;
import org.example.age.service.component.stub.StubAvsComponent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class AvsApiEndpointTest {

    @RegisterExtension
    private static final TestServer<?> avsServer =
            TestUndertowServer.register("avs", "/api/", StubAvsComponent::createApiHandler);

    @Test
    public void verificationState() throws IOException {
        HttpOptional<VerificationState> maybeState = JsonApiClient.requestBuilder(
                        new TypeReference<VerificationState>() {})
                .get(avsServer.url("/api/verification-state"))
                .header("Account-Id", "username")
                .header("User-Agent", "agent")
                .build()
                .execute();
        assertThat(maybeState).isPresent();
    }

    @Test
    public void verificationSession() throws IOException {
        HttpOptional<VerificationSession> maybeSession = JsonApiClient.requestBuilder(
                        new TypeReference<VerificationSession>() {})
                .post(avsServer.url("/api/verification-session?site-id=Site"))
                .build()
                .execute();
        assertThat(maybeSession).isPresent();
    }

    @Test
    public void linkedVerificationRequest() throws IOException {
        SecureId requestId = SecureId.generate();
        int statusCode = JsonApiClient.requestBuilder()
                .post(avsServer.url("/api/linked-verification-request?request-id=%s", requestId))
                .header("Account-Id", "username")
                .header("User-Agent", "agent")
                .build()
                .execute();
        assertThat(statusCode).isEqualTo(200);
    }

    @Test
    public void ageCertificate() throws IOException {
        HttpOptional<String> maybeRedirectUrl = JsonApiClient.requestBuilder(new TypeReference<String>() {})
                .post(avsServer.url("/api/age-certificate"))
                .header("Account-Id", "username")
                .header("User-Agent", "agent")
                .build()
                .execute();
        assertThat(maybeRedirectUrl).isPresent();
    }
}
