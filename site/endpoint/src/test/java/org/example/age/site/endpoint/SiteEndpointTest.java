package org.example.age.site.endpoint;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.age.testing.client.WebStageTesting.await;

import dagger.BindsInstance;
import dagger.Component;
import jakarta.inject.Singleton;
import java.time.Duration;
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

    private final SiteApi endpoint = TestComponent.create(new FakeAvsClient());

    @Test
    public void verify() {
        VerificationRequest request = await(endpoint.createVerificationRequest());

        VerifiedUser user = VerifiedUser.builder()
                .pseudonym(SecureId.fromString("keXeY3kiQDgOhenFw9GMFv3zUFSCSsqrcsmwf3DvpdA"))
                .ageRange(AgeRange.builder().min(18).build())
                .build();
        AgeCertificate ageCertificate =
                AgeCertificate.builder().request(request).user(user).build();
        SignedAgeCertificate signedAgeCertificate = TestSignatures.sign(ageCertificate);
        await(endpoint.processAgeCertificate(signedAgeCertificate));

        VerificationState state = await(endpoint.getVerificationState());
        assertThat(state.getStatus()).isEqualTo(VerificationStatus.VERIFIED);
        VerifiedUser expectedUser = VerifiedUser.builder()
                .pseudonym(SecureId.fromString("wqhgWlb9wYtzTDYbGeYFJJvS4xjmQsp3cf3ntbcBuNI"))
                .ageRange(AgeRange.builder().min(18).build())
                .build();
        assertThat(state.getUser()).isEqualTo(expectedUser);
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

        static SiteApi create(AvsApi avsClient) {
            AccountIdContext accountIdContext = () -> "username";
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
