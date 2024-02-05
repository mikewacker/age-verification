package org.example.age.service.endpoint;

import static io.github.mikewacker.drift.testing.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.mikewacker.drift.api.HttpOptional;
import io.github.mikewacker.drift.client.JsonApiClient;
import io.github.mikewacker.drift.testing.server.TestServer;
import java.io.IOException;
import org.example.age.api.def.VerificationState;
import org.example.age.api.def.VerificationStatus;
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
        HttpOptional<VerificationState> maybeSiteState = JsonApiClient.requestBuilder()
                .jsonResponse(new TypeReference<VerificationState>() {})
                .get(siteServer.url("/api/verification-state"))
                .header("Account-Id", siteAccountId)
                .build()
                .execute();
        assertThat(maybeSiteState).isPresent();
        VerificationState siteState = maybeSiteState.get();
        assertThat(siteState.status()).isEqualTo(VerificationStatus.UNVERIFIED);

        HttpOptional<VerificationState> maybeAvsState = JsonApiClient.requestBuilder()
                .jsonResponse(new TypeReference<VerificationState>() {})
                .get(avsServer.url("/api/verification-state"))
                .header("Account-Id", avsAccountId)
                .build()
                .execute();
        assertThat(maybeAvsState).isPresent();
        VerificationState avsState = maybeAvsState.get();
        assertThat(avsState.status()).isEqualTo(VerificationStatus.VERIFIED);

        // Verify age.
        HttpOptional<VerificationRequest> maybeRequest = JsonApiClient.requestBuilder()
                .jsonResponse(new TypeReference<VerificationRequest>() {})
                .post(siteServer.url("/api/verification-request/create"))
                .header("Account-Id", siteAccountId)
                .build()
                .execute();
        assertThat(maybeRequest).isPresent();
        VerificationRequest request = maybeRequest.get();

        int linkStatusCode = JsonApiClient.requestBuilder()
                .statusCodeResponse()
                .post(request.redirectUrl())
                .header("Account-Id", avsAccountId)
                .build()
                .execute();
        assertThat(linkStatusCode).isEqualTo(200);

        HttpOptional<String> maybeRedirectUrl = JsonApiClient.requestBuilder()
                .jsonResponse(new TypeReference<String>() {})
                .post(avsServer.url("/api/age-certificate/send"))
                .header("Account-Id", avsAccountId)
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
        HttpOptional<VerificationState> maybeNewSiteState = JsonApiClient.requestBuilder()
                .jsonResponse(new TypeReference<VerificationState>() {})
                .get(redirectUrl)
                .header("Account-Id", siteAccountId)
                .build()
                .execute();
        assertThat(maybeNewSiteState).isPresent();
        VerificationState newSiteState = maybeNewSiteState.get();
        assertThat(newSiteState.status()).isEqualTo(VerificationStatus.VERIFIED);
    }

    // static class
    private ServiceEndpointTestTemplate() {}
}
