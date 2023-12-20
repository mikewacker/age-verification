package org.example.age.service.endpoint;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.age.testing.api.HttpOptionalAssert.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import java.util.Map;
import org.example.age.api.base.HttpOptional;
import org.example.age.api.def.VerificationState;
import org.example.age.api.def.VerificationStatus;
import org.example.age.data.certificate.VerificationRequest;
import org.example.age.testing.client.TestClient;
import org.example.age.testing.server.TestServer;

/** Test template for service endpoint tests. */
public final class ServiceEndpointTestTemplate {

    public static void verify() throws IOException {
        verify("publius-jr", "Billy Smith", true);
    }

    public static void verifyFailed_DuplicateVerification() throws IOException {
        verify("publius", "John Smith", true);
        verify("drop-table", "John Smith", false);
    }

    private static void verify(String siteAccountId, String avsAccountId, boolean succeeds) throws IOException {
        TestServer<?> siteServer = TestServer.get("site");
        TestServer<?> avsServer = TestServer.get("avs");

        // Check initial state.
        HttpOptional<VerificationState> maybeSiteState = TestClient.requestBuilder(
                        new TypeReference<VerificationState>() {})
                .get(siteServer.url("/api/verification-state"))
                .headers(Map.of("Account-Id", siteAccountId))
                .execute();
        assertThat(maybeSiteState).isPresent();
        VerificationState siteState = maybeSiteState.get();
        assertThat(siteState.status()).isEqualTo(VerificationStatus.UNVERIFIED);

        HttpOptional<VerificationState> maybeAvsState = TestClient.requestBuilder(
                        new TypeReference<VerificationState>() {})
                .get(avsServer.url("/api/verification-state"))
                .headers(Map.of("Account-Id", avsAccountId))
                .execute();
        assertThat(maybeAvsState).isPresent();
        VerificationState avsState = maybeAvsState.get();
        assertThat(avsState.status()).isEqualTo(VerificationStatus.VERIFIED);

        // Verify age.
        HttpOptional<VerificationRequest> maybeRequest = TestClient.requestBuilder(
                        new TypeReference<VerificationRequest>() {})
                .post(siteServer.url("/api/verification-request"))
                .headers(Map.of("Account-Id", siteAccountId))
                .execute();
        assertThat(maybeRequest).isPresent();
        VerificationRequest request = maybeRequest.get();

        int linkStatusCode = TestClient.requestBuilder()
                .post(request.redirectUrl())
                .headers(Map.of("Account-Id", avsAccountId))
                .execute();
        assertThat(linkStatusCode).isEqualTo(200);

        HttpOptional<String> maybeRedirectUrl = TestClient.requestBuilder(new TypeReference<String>() {})
                .post(avsServer.url("/api/age-certificate"))
                .headers(Map.of("Account-Id", avsAccountId))
                .execute();
        if (succeeds) {
            assertThat(maybeRedirectUrl).isPresent();
        } else {
            assertThat(maybeRedirectUrl).isEmptyWithErrorCode(409);
            return;
        }
        String redirectUrl = maybeRedirectUrl.get();

        // Check updated state.
        HttpOptional<VerificationState> maybeNewSiteState = TestClient.requestBuilder(
                        new TypeReference<VerificationState>() {})
                .get(redirectUrl)
                .headers(Map.of("Account-Id", siteAccountId))
                .execute();
        assertThat(maybeNewSiteState).isPresent();
        VerificationState newSiteState = maybeNewSiteState.get();
        assertThat(newSiteState.status()).isEqualTo(VerificationStatus.VERIFIED);
    }

    // static class
    private ServiceEndpointTestTemplate() {}
}
