package org.example.age.service.site.endpoint;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.age.testing.api.HttpOptionalAssert.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import java.util.Map;
import org.example.age.api.base.HttpOptional;
import org.example.age.api.common.VerificationState;
import org.example.age.api.common.VerificationStatus;
import org.example.age.data.certificate.VerificationSession;
import org.example.age.data.crypto.SecureId;
import org.example.age.service.avs.endpoint.test.FakeAvsComponent;
import org.example.age.service.site.endpoint.test.TestSiteComponent;
import org.example.age.testing.client.TestClient;
import org.example.age.testing.server.TestServer;
import org.example.age.testing.server.undertow.TestUndertowServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class SiteServiceEndpointTest {

    @RegisterExtension
    private static final TestServer<?> siteServer =
            TestUndertowServer.register("site", TestSiteComponent::createApiHandler, "/api/");

    @RegisterExtension
    private static final TestServer<?> fakeAvsServer =
            TestUndertowServer.register("avs", FakeAvsComponent::createApiHandler, "/api/");

    @Test
    public void verify() throws IOException {
        verify("publius-jr", "Billy Smith", 200);
    }

    @Test
    public void verifyFailed_DuplicateVerification() throws IOException {
        verify("publius", "John Smith", 200);
        verify("drop-table", "John Smith", 409);
    }

    private void verify(String siteAccountId, String avsAccountId, int expectedStatusCode) throws IOException {
        HttpOptional<VerificationSession> maybeSession = TestClient.requestBuilder()
                .post(siteServer.url("/api/verification-session"))
                .headers(Map.of("Account-Id", siteAccountId))
                .executeWithJsonResponse(new TypeReference<>() {});
        assertThat(maybeSession).isPresent();
        VerificationSession session = maybeSession.get();

        SecureId requestId = session.verificationRequest().id();
        int linkStatusCode = TestClient.requestBuilder()
                .post(fakeAvsServer.url("/api/linked-verification-request?request-id=%s", requestId))
                .headers(Map.of("Account-Id", avsAccountId))
                .executeWithStatusCodeResponse();
        assertThat(linkStatusCode).isEqualTo(200);

        int certificateStatusCode = TestClient.requestBuilder()
                .post(fakeAvsServer.url("/api/age-certificate"))
                .headers(Map.of("Account-Id", avsAccountId))
                .executeWithStatusCodeResponse();
        assertThat(certificateStatusCode).isEqualTo(expectedStatusCode);
        if (expectedStatusCode != 200) {
            return;
        }

        HttpOptional<VerificationState> maybeState = TestClient.requestBuilder()
                .get(siteServer.url("/api/verification-state"))
                .headers(Map.of("Account-Id", siteAccountId))
                .executeWithJsonResponse(new TypeReference<>() {});
        assertThat(maybeState).isPresent();
        VerificationState state = maybeState.get();
        assertThat(state.status()).isEqualTo(VerificationStatus.VERIFIED);
    }
}
