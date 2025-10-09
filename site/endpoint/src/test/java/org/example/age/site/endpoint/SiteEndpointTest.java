package org.example.age.site.endpoint;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.example.age.testing.client.WebStageTesting.await;
import static org.example.age.testing.client.WebStageTesting.awaitErrorCode;

import dagger.BindsInstance;
import dagger.Component;
import jakarta.inject.Singleton;
import jakarta.ws.rs.NotAuthorizedException;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.function.Supplier;
import org.example.age.avs.api.client.AvsApi;
import org.example.age.common.api.AgeCertificate;
import org.example.age.common.api.AgeRange;
import org.example.age.common.api.SignedAgeCertificate;
import org.example.age.common.api.VerificationRequest;
import org.example.age.common.api.VerifiedUser;
import org.example.age.common.api.crypto.SecureId;
import org.example.age.common.provider.pendingstore.test.TestPendingStoreModule;
import org.example.age.common.spi.AccountIdContext;
import org.example.age.site.api.SiteApi;
import org.example.age.site.api.VerificationState;
import org.example.age.site.api.VerificationStatus;
import org.example.age.site.provider.accountstore.test.TestSiteAccountStoreModule;
import org.example.age.site.provider.certificateverifier.test.TestCertificateVerifierModule;
import org.example.age.site.provider.userlocalizer.test.TestSiteUserLocalizerModule;
import org.example.age.testing.api.TestModels;
import org.example.age.testing.api.TestSignatures;
import org.example.age.testing.client.TestAsyncEndpoints;
import org.junit.jupiter.api.Test;
import retrofit2.Call;
import retrofit2.mock.Calls;

public final class SiteEndpointTest {

    private final SiteApi endpoint = TestComponent.create(this::getAccountId, new FakeAvsClient());

    private String accountId = "username";

    @Test
    public void verify() {
        VerificationState initState = await(endpoint.getVerificationState());
        assertThat(initState.getStatus()).isEqualTo(VerificationStatus.UNVERIFIED);

        VerificationRequest request = await(endpoint.createVerificationRequest());
        assertThat(request.getSiteId()).isEqualTo("site");

        SignedAgeCertificate signedAgeCertificate = createSignedAgeCertificate(request);
        await(endpoint.processAgeCertificate(signedAgeCertificate));
        VerificationState state = await(endpoint.getVerificationState());
        assertThat(state.getStatus()).isEqualTo(VerificationStatus.VERIFIED);
        VerifiedUser expectedUser = VerifiedUser.builder()
                .pseudonym(SecureId.fromString("wqhgWlb9wYtzTDYbGeYFJJvS4xjmQsp3cf3ntbcBuNI"))
                .ageRange(AgeRange.builder().min(18).build())
                .build();
        assertThat(state.getUser()).isEqualTo(expectedUser);
        OffsetDateTime expectedExpiration = OffsetDateTime.now(ZoneOffset.UTC).plus(Duration.ofDays(30));
        assertThat(state.getExpiration()).isCloseTo(expectedExpiration, within(1, ChronoUnit.SECONDS));
    }

    @Test
    public void error_DuplicateVerification() {
        accountId = "duplicate";
        VerificationRequest request = await(endpoint.createVerificationRequest());
        SignedAgeCertificate signedAgeCertificate = createSignedAgeCertificate(request);
        awaitErrorCode(endpoint.processAgeCertificate(signedAgeCertificate), 409);
    }

    @Test
    public void error_Unauthenticated() {
        accountId = null;
        awaitErrorCode(endpoint.getVerificationState(), 401);
        awaitErrorCode(endpoint.createVerificationRequest(), 401);
    }

    @Test
    public void error_AccountNotFound() {
        VerificationRequest request = TestModels.createVerificationRequest("site");
        SignedAgeCertificate signedAgeCertificate = createSignedAgeCertificate(request);
        awaitErrorCode(endpoint.processAgeCertificate(signedAgeCertificate), 404);
    }

    @Test
    public void error_WrongSite() {
        VerificationRequest request = TestModels.createVerificationRequest("other-site");
        SignedAgeCertificate signedAgeCertificate = createSignedAgeCertificate(request);
        awaitErrorCode(endpoint.processAgeCertificate(signedAgeCertificate), 403);
    }

    @Test
    public void error_ExpiredAgeCertificate() {
        VerificationRequest request = createExpiredVerificationRequest();
        SignedAgeCertificate signedAgeCertificate = createSignedAgeCertificate(request);
        awaitErrorCode(endpoint.processAgeCertificate(signedAgeCertificate), 404);
    }

    @Test
    public void error_InvalidSignature() {
        VerificationRequest request = TestModels.createVerificationRequest("site");
        SignedAgeCertificate signedAgeCertificate = createInvalidSignedAgeCertificate(request);
        awaitErrorCode(endpoint.processAgeCertificate(signedAgeCertificate), 401);
    }

    private SignedAgeCertificate createSignedAgeCertificate(VerificationRequest request) {
        VerifiedUser user = VerifiedUser.builder()
                .pseudonym(SecureId.fromString("keXeY3kiQDgOhenFw9GMFv3zUFSCSsqrcsmwf3DvpdA"))
                .ageRange(AgeRange.builder().min(18).build())
                .build();
        AgeCertificate ageCertificate =
                AgeCertificate.builder().request(request).user(user).build();
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
                .siteId("site")
                .expiration(expiration)
                .build();
    }

    private String getAccountId() {
        return Optional.ofNullable(accountId)
                .orElseThrow(() -> new NotAuthorizedException("failed to authenticate account"));
    }

    /** Fake client for {@link AvsApi}. */
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

    /** Dagger component for the {@link SiteApi} endpoint. */
    @Component(
            modules = {
                SiteEndpointModule.class,
                TestSiteAccountStoreModule.class,
                TestPendingStoreModule.class,
                TestCertificateVerifierModule.class,
                TestSiteUserLocalizerModule.class,
            })
    @Singleton
    interface TestComponent extends Supplier<SiteApi> {

        static SiteApi create(AccountIdContext accountIdContext, AvsApi avsClient) {
            SiteEndpointConfig config = SiteEndpointConfig.builder()
                    .id("site")
                    .verifiedAccountExpiresIn(Duration.ofDays(30))
                    .build();
            SiteApi endpoint = DaggerSiteEndpointTest_TestComponent.factory()
                    .create(accountIdContext, avsClient, config)
                    .get();
            return TestAsyncEndpoints.test(endpoint, SiteApi.class);
        }

        @Component.Factory
        interface Factory {

            TestComponent create(
                    @BindsInstance AccountIdContext accountIdContext,
                    @BindsInstance AvsApi avsClient,
                    @BindsInstance SiteEndpointConfig config);
        }
    }
}
