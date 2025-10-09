package org.example.age.avs.endpoint;

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
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import org.example.age.avs.api.AvsApi;
import org.example.age.avs.provider.accountstore.test.TestAvsAccountStoreModule;
import org.example.age.avs.provider.certificatesigner.test.TestCertificateSignerModule;
import org.example.age.avs.provider.userlocalizer.test.TestAvsUserLocalizerModule;
import org.example.age.common.api.AgeCertificate;
import org.example.age.common.api.AgeRange;
import org.example.age.common.api.AgeThresholds;
import org.example.age.common.api.SignedAgeCertificate;
import org.example.age.common.api.VerificationRequest;
import org.example.age.common.api.VerifiedUser;
import org.example.age.common.api.crypto.SecureId;
import org.example.age.common.provider.pendingstore.test.TestPendingStoreModule;
import org.example.age.common.spi.AccountIdContext;
import org.example.age.site.api.VerificationState;
import org.example.age.site.api.client.SiteApi;
import org.example.age.testing.api.TestSignatures;
import org.example.age.testing.client.TestAsyncEndpoints;
import org.junit.jupiter.api.Test;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.mock.Calls;

public final class AvsEndpointTest {

    private final AvsApi endpoint = TestComponent.create(this::getAccountId, FakeSiteClient::new);

    private String accountId = "person";
    private AgeCertificate ageCertificate = null;

    @Test
    public void verify() {
        VerificationRequest request = await(endpoint.createVerificationRequestForSite("site"));
        assertThat(request.getSiteId()).isEqualTo("site");
        OffsetDateTime expectedExpiration = OffsetDateTime.now(ZoneOffset.UTC).plus(Duration.ofMinutes(5));
        assertThat(request.getExpiration()).isCloseTo(expectedExpiration, within(1, ChronoUnit.SECONDS));

        await(endpoint.linkVerificationRequest(request.getId()));
        await(endpoint.sendAgeCertificate());
        VerifiedUser expectedUser = VerifiedUser.builder()
                .pseudonym(SecureId.fromString("keXeY3kiQDgOhenFw9GMFv3zUFSCSsqrcsmwf3DvpdA"))
                .ageRange(AgeRange.builder().min(18).build())
                .build();
        AgeCertificate expectedAgeCertificate =
                AgeCertificate.builder().request(request).user(expectedUser).build();
        assertThat(ageCertificate).isEqualTo(expectedAgeCertificate);
    }

    @Test
    public void verify_DifferentSite() {
        VerificationRequest request = await(endpoint.createVerificationRequestForSite("other-site"));
        await(endpoint.linkVerificationRequest(request.getId()));
        await(endpoint.sendAgeCertificate());
        assertThat(ageCertificate.getRequest()).isEqualTo(request);
        VerifiedUser expectedUser = VerifiedUser.builder()
                .pseudonym(SecureId.fromString("iaDG-BXou0kKr5gg2j0BJj0RKsa00bVvnpbRCiEism4"))
                .ageRange(AgeRange.builder().min(18).build())
                .build();
        AgeCertificate expectedAgeCertificate =
                AgeCertificate.builder().request(request).user(expectedUser).build();
        assertThat(ageCertificate).isEqualTo(expectedAgeCertificate);
    }

    @Test
    public void error_Unauthenticated() {
        accountId = null;
        awaitErrorCode(endpoint.linkVerificationRequest(SecureId.generate()), 401);
        awaitErrorCode(endpoint.sendAgeCertificate(), 401);
    }

    @Test
    public void error_UnverifiedPerson() {
        accountId = "unverified-person";
        awaitErrorCode(endpoint.linkVerificationRequest(SecureId.generate()), 403);
        awaitErrorCode(endpoint.sendAgeCertificate(), 403);
    }

    @Test
    public void error_LinkVerificationRequestTwice() {
        VerificationRequest request = await(endpoint.createVerificationRequestForSite("site"));
        await(endpoint.linkVerificationRequest(request.getId()));
        awaitErrorCode(endpoint.linkVerificationRequest(request.getId()), 404);
    }

    @Test
    public void error_SendAgeCertificateTwice() {
        VerificationRequest request = await(endpoint.createVerificationRequestForSite("site"));
        await(endpoint.linkVerificationRequest(request.getId()));
        await(endpoint.sendAgeCertificate());
        awaitErrorCode(endpoint.sendAgeCertificate(), 404);
    }

    @Test
    public void error_VerificationRequestNotFound() {
        awaitErrorCode(endpoint.linkVerificationRequest(SecureId.generate()), 404);
        awaitErrorCode(endpoint.sendAgeCertificate(), 404);
    }

    @Test
    public void error_UnregisteredSite() {
        awaitErrorCode(endpoint.createVerificationRequestForSite("unregistered-site"), 404);
    }

    private String getAccountId() {
        return Optional.ofNullable(accountId)
                .orElseThrow(() -> new NotAuthorizedException("failed to authenticate account"));
    }

    /** Fake client for {@link SiteApi}. */
    private final class FakeSiteClient implements SiteApi {

        @Override
        public Call<VerificationState> getVerificationState() {
            return Calls.failure(new UnsupportedOperationException());
        }

        @Override
        public Call<VerificationRequest> createVerificationRequest() {
            return Calls.failure(new UnsupportedOperationException());
        }

        @Override
        public Call<Void> processAgeCertificate(SignedAgeCertificate signedAgeCertificate) {
            ageCertificate = TestSignatures.verify(signedAgeCertificate);
            return Calls.response(Response.success(null));
        }
    }

    /** Dagger component for the {@link AvsApi} endpoint. */
    @Component(
            modules = {
                AvsEndpointModule.class,
                TestAvsAccountStoreModule.class,
                TestPendingStoreModule.class,
                TestCertificateSignerModule.class,
                TestAvsUserLocalizerModule.class
            })
    @Singleton
    interface TestComponent extends Supplier<AvsApi> {

        static AvsApi create(AccountIdContext accountIdContext, Supplier<SiteApi> siteClientFactory) {
            Map<String, SiteApi> siteClients =
                    Map.of("site", siteClientFactory.get(), "other-site", siteClientFactory.get());
            AvsEndpointConfig config = AvsEndpointConfig.builder()
                    .verificationRequestExpiresIn(Duration.ofMinutes(5))
                    .ageThresholds(Map.of("site", AgeThresholds.of(18), "other-site", AgeThresholds.of(18)))
                    .build();
            AvsApi endpoint = DaggerAvsEndpointTest_TestComponent.factory()
                    .create(accountIdContext, siteClients, config)
                    .get();
            return TestAsyncEndpoints.test(endpoint, AvsApi.class);
        }

        @Component.Factory
        interface Factory {

            TestComponent create(
                    @BindsInstance AccountIdContext accountIdContext,
                    @BindsInstance Map<String, SiteApi> siteClients,
                    @BindsInstance AvsEndpointConfig config);
        }
    }
}
