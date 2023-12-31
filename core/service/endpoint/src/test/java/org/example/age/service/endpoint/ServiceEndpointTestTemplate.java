package org.example.age.service.endpoint;

import static io.github.mikewacker.drift.testing.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.mikewacker.drift.api.HttpOptional;
import io.github.mikewacker.drift.testing.server.TestServer;
import java.io.IOException;
import java.util.Map;
import org.example.age.api.def.VerificationState;
import org.example.age.api.def.VerificationStatus;
import org.example.age.client.infra.JsonApiClient;
import org.example.age.data.certificate.VerificationRequest;

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
        HttpOptional<VerificationState> maybeSiteState = JsonApiClient.requestBuilder(
                        new TypeReference<VerificationState>() {})
                .get(siteServer.url("/api/verification-state"))
                .headers(Map.of("Account-Id", siteAccountId))
                .build()
                .execute();
        assertThat(maybeSiteState).isPresent();
        VerificationState siteState = maybeSiteState.get();
        assertThat(siteState.status()).isEqualTo(VerificationStatus.UNVERIFIED);

        HttpOptional<VerificationState> maybeAvsState = JsonApiClient.requestBuilder(
                        new TypeReference<VerificationState>() {})
                .get(avsServer.url("/api/verification-state"))
                .headers(Map.of("Account-Id", avsAccountId))
                .build()
                .execute();
        assertThat(maybeAvsState).isPresent();
        VerificationState avsState = maybeAvsState.get();
        assertThat(avsState.status()).isEqualTo(VerificationStatus.VERIFIED);

        // Verify age.
        HttpOptional<VerificationRequest> maybeRequest = JsonApiClient.requestBuilder(
                        new TypeReference<VerificationRequest>() {})
                .post(siteServer.url("/api/verification-request"))
                .headers(Map.of("Account-Id", siteAccountId))
                .build()
                .execute();
        assertThat(maybeRequest).isPresent();
        VerificationRequest request = maybeRequest.get();

        int linkStatusCode = JsonApiClient.requestBuilder()
                .post(request.redirectUrl())
                .headers(Map.of("Account-Id", avsAccountId))
                .build()
                .execute();
        assertThat(linkStatusCode).isEqualTo(200);

        HttpOptional<String> maybeRedirectUrl = JsonApiClient.requestBuilder(new TypeReference<String>() {})
                .post(avsServer.url("/api/age-certificate"))
                .headers(Map.of("Account-Id", avsAccountId))
                .build()
                .execute();
        if (succeeds) {
            assertThat(maybeRedirectUrl).isPresent();
        } else {
            assertThat(maybeRedirectUrl).isEmptyWithErrorCode(409);
            return;
        }
        String redirectUrl = maybeRedirectUrl.get();

        // Check updated state.
        HttpOptional<VerificationState> maybeNewSiteState = JsonApiClient.requestBuilder(
                        new TypeReference<VerificationState>() {})
                .get(redirectUrl)
                .headers(Map.of("Account-Id", siteAccountId))
                .build()
                .execute();
        assertThat(maybeNewSiteState).isPresent();
        VerificationState newSiteState = maybeNewSiteState.get();
        assertThat(newSiteState.status()).isEqualTo(VerificationStatus.VERIFIED);
    }

    // static class
    private ServiceEndpointTestTemplate() {}
}
