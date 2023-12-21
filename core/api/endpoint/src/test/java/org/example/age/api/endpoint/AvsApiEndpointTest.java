package org.example.age.api.endpoint;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.age.testing.api.HttpOptionalAssert.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import java.util.Map;
import org.example.age.api.base.HttpOptional;
import org.example.age.api.def.VerificationState;
import org.example.age.data.certificate.VerificationSession;
import org.example.age.data.crypto.SecureId;
import org.example.age.service.component.stub.StubAvsComponent;
import org.example.age.testing.client.TestClient;
import org.example.age.testing.server.TestServer;
import org.example.age.testing.server.undertow.TestUndertowServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class AvsApiEndpointTest {

    @RegisterExtension
    private static final TestServer<?> avsServer =
            TestUndertowServer.register("avs", "/api/", StubAvsComponent::createApiHandler);

    @Test
    public void verificationState() throws IOException {
        HttpOptional<VerificationState> maybeState = TestClient.requestBuilder(
                        new TypeReference<VerificationState>() {})
                .get(avsServer.url("/api/verification-state"))
                .headers(Map.of("Account-Id", "username", "User-Agent", "agent"))
                .execute();
        assertThat(maybeState).isPresent();
    }

    @Test
    public void verificationSession() throws IOException {
        HttpOptional<VerificationSession> maybeSession = TestClient.requestBuilder(
                        new TypeReference<VerificationSession>() {})
                .post(avsServer.url("/api/verification-session?site-id=Site"))
                .execute();
        assertThat(maybeSession).isPresent();
    }

    @Test
    public void linkedVerificationRequest() throws IOException {
        SecureId requestId = SecureId.generate();
        int statusCode = TestClient.requestBuilder()
                .post(avsServer.url("/api/linked-verification-request?request-id=%s", requestId))
                .headers(Map.of("Account-Id", "username", "User-Agent", "agent"))
                .execute();
        assertThat(statusCode).isEqualTo(200);
    }

    @Test
    public void ageCertificate() throws IOException {
        HttpOptional<String> maybeRedirectUrl = TestClient.requestBuilder(new TypeReference<String>() {})
                .post(avsServer.url("/api/age-certificate"))
                .headers(Map.of("Account-Id", "username", "User-Agent", "agent"))
                .execute();
        assertThat(maybeRedirectUrl).isPresent();
    }
}