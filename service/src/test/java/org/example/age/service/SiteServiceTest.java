package org.example.age.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.example.age.testing.util.WebStageTesting.await;
import static org.example.age.testing.util.WebStageTesting.awaitErrorCode;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import org.example.age.avs.api.client.AvsApi;
import org.example.age.common.api.AgeCertificate;
import org.example.age.common.api.SignedAgeCertificate;
import org.example.age.common.api.VerificationRequest;
import org.example.age.common.api.crypto.SecureId;
import org.example.age.service.testing.TestSiteService;
import org.example.age.service.testing.TestSiteServiceComponent;
import org.example.age.site.api.VerificationState;
import org.example.age.site.api.VerificationStatus;
import org.example.age.testing.api.TestModels;
import org.example.age.testing.api.TestSignatures;
import org.junit.jupiter.api.Test;
import retrofit2.Call;
import retrofit2.mock.Calls;

public final class SiteServiceTest {

    private final TestSiteService siteService = TestSiteServiceComponent.create(new FakeAvsClient());

    @Test
    public void verify() {
        siteService.setAccountId("username");
        VerificationState initState = await(siteService.getVerificationState());
        assertThat(initState.getStatus()).isEqualTo(VerificationStatus.UNVERIFIED);

        VerificationRequest request = await(siteService.createVerificationRequest());
        assertThat(request.getSiteId()).isEqualTo("site1");

        SignedAgeCertificate signedAgeCertificate = createSignedAgeCertificate(request);
        await(siteService.processAgeCertificate(signedAgeCertificate));
        VerificationState state = await(siteService.getVerificationState());
        assertThat(state.getStatus()).isEqualTo(VerificationStatus.VERIFIED);
        assertThat(state.getUser().getPseudonym())
                .isNotEqualTo(signedAgeCertificate.getAgeCertificate().getUser().getPseudonym());
        OffsetDateTime expectedExpiration = OffsetDateTime.now(ZoneOffset.UTC).plus(Duration.ofDays(30));
        assertThat(state.getExpiration()).isCloseTo(expectedExpiration, within(1, ChronoUnit.SECONDS));
    }

    @Test
    public void error_DuplicateVerification() {
        siteService.setAccountId("duplicate");
        VerificationRequest request = await(siteService.createVerificationRequest());
        SignedAgeCertificate signedAgeCertificate = createSignedAgeCertificate(request);
        awaitErrorCode(siteService.processAgeCertificate(signedAgeCertificate), 409);
    }

    @Test
    public void error_Unauthenticated() {
        awaitErrorCode(siteService.getVerificationState(), 401);
        awaitErrorCode(siteService.createVerificationRequest(), 401);
    }

    @Test
    public void error_AccountNotFound() {
        VerificationRequest request = TestModels.createVerificationRequest("site1");
        SignedAgeCertificate signedAgeCertificate = createSignedAgeCertificate(request);
        awaitErrorCode(siteService.processAgeCertificate(signedAgeCertificate), 404);
    }

    @Test
    public void error_WrongSite() {
        VerificationRequest request = TestModels.createVerificationRequest("site2");
        SignedAgeCertificate signedAgeCertificate = createSignedAgeCertificate(request);
        awaitErrorCode(siteService.processAgeCertificate(signedAgeCertificate), 403);
    }

    @Test
    public void error_ExpiredAgeCertificate() {
        VerificationRequest request = createExpiredVerificationRequest();
        SignedAgeCertificate signedAgeCertificate = createSignedAgeCertificate(request);
        awaitErrorCode(siteService.processAgeCertificate(signedAgeCertificate), 404);
    }

    @Test
    public void error_InvalidSignature() {
        VerificationRequest request = TestModels.createVerificationRequest("site1");
        SignedAgeCertificate signedAgeCertificate = createInvalidSignedAgeCertificate(request);
        awaitErrorCode(siteService.processAgeCertificate(signedAgeCertificate), 401);
    }

    private SignedAgeCertificate createSignedAgeCertificate(VerificationRequest request) {
        AgeCertificate ageCertificate = TestModels.createAgeCertificate(request);
        return TestSignatures.sign(ageCertificate);
    }

    private static SignedAgeCertificate createInvalidSignedAgeCertificate(VerificationRequest request) {
        AgeCertificate ageCertificate = TestModels.createAgeCertificate(request);
        return TestSignatures.signInvalid(ageCertificate, "secp256r1");
    }

    private static VerificationRequest createExpiredVerificationRequest() {
        OffsetDateTime expiration = OffsetDateTime.now(ZoneOffset.UTC).plus(Duration.ofMinutes(-5));
        return VerificationRequest.builder()
                .id(SecureId.generate())
                .siteId("site1")
                .expiration(expiration)
                .build();
    }

    /** Fake client implementation of {@link AvsApi}. */
    private static final class FakeAvsClient implements AvsApi {

        @Override
        public Call<VerificationRequest> createVerificationRequestForSite(String siteId) {
            VerificationRequest request = TestModels.createVerificationRequest(siteId);
            return Calls.response(request);
        }

        @Override
        public Call<Void> linkVerificationRequest(SecureId requestId) {
            return Calls.failure(new UnsupportedOperationException());
        }

        @Override
        public Call<Void> sendAgeCertificate() {
            return Calls.failure(new UnsupportedOperationException());
        }
    }
}
