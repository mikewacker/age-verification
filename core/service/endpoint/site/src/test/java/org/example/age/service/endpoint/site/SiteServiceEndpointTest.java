package org.example.age.service.endpoint.site;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.age.testing.api.HttpOptionalAssert.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import java.util.Map;
import org.example.age.api.base.HttpOptional;
import org.example.age.api.def.common.VerificationState;
import org.example.age.api.def.common.VerificationStatus;
import org.example.age.data.certificate.VerificationRequest;
import org.example.age.service.endpoint.avs.fake.FakeAvsComponent;
import org.example.age.service.endpoint.site.test.TestSiteComponent;
import org.example.age.testing.client.TestClient;
import org.example.age.testing.server.TestServer;
import org.example.age.testing.server.undertow.TestUndertowServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class SiteServiceEndpointTest {

    @RegisterExtension
    private static final TestServer<?> siteServer =
            TestUndertowServer.register("site", "/api/", TestSiteComponent::createApiHandler);

    @RegisterExtension
    private static final TestServer<?> fakeAvsServer =
            TestUndertowServer.register("avs", "/api/", FakeAvsComponent::createApiHandler);

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
        HttpOptional<VerificationState> maybeAvsState = TestClient.requestBuilder(
                        new TypeReference<VerificationState>() {})
                .get(fakeAvsServer.url("/api/verification-state"))
                .headers(Map.of("Account-Id", avsAccountId))
                .execute();
        assertThat(maybeAvsState).isPresent();
        VerificationState avsState = maybeAvsState.get();
        assertThat(avsState.status()).isEqualTo(VerificationStatus.VERIFIED);

        HttpOptional<VerificationRequest> maybeRequest = TestClient.requestBuilder(
                        new TypeReference<VerificationRequest>() {})
                .post(siteServer.url("/api/verification-request"))
                .headers(Map.of("Account-Id", siteAccountId))
                .execute();
        assertThat(maybeRequest).isPresent();
        VerificationRequest request = maybeRequest.get();

        int linkStatusCode = TestClient.requestBuilder()
                .post(fakeAvsServer.url("/api/linked-verification-request?request-id=%s", request.id()))
                .headers(Map.of("Account-Id", avsAccountId))
                .execute();
        assertThat(linkStatusCode).isEqualTo(200);

        int certificateStatusCode = TestClient.requestBuilder()
                .post(fakeAvsServer.url("/api/age-certificate"))
                .headers(Map.of("Account-Id", avsAccountId))
                .execute();
        assertThat(certificateStatusCode).isEqualTo(expectedStatusCode);
        if (expectedStatusCode != 200) {
            return;
        }

        HttpOptional<VerificationState> maybeSiteState = TestClient.requestBuilder(
                        new TypeReference<VerificationState>() {})
                .get(siteServer.url("/api/verification-state"))
                .headers(Map.of("Account-Id", siteAccountId))
                .execute();
        assertThat(maybeSiteState).isPresent();
        VerificationState state = maybeSiteState.get();
        assertThat(state.status()).isEqualTo(VerificationStatus.VERIFIED);
    }
}
