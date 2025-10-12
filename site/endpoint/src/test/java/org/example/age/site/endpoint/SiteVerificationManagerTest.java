package org.example.age.site.endpoint;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.example.age.testing.client.WebStageTesting.await;
import static org.example.age.testing.client.WebStageTesting.awaitErrorCode;

import dagger.BindsInstance;
import dagger.Component;
import jakarta.inject.Singleton;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.function.Supplier;
import org.example.age.common.api.AgeCertificate;
import org.example.age.common.api.AgeRange;
import org.example.age.common.api.VerificationRequest;
import org.example.age.common.api.VerifiedUser;
import org.example.age.common.api.crypto.SecureId;
import org.example.age.common.provider.pendingstore.test.TestPendingStoreModule;
import org.example.age.site.api.VerificationState;
import org.example.age.site.api.VerificationStatus;
import org.example.age.site.provider.accountstore.test.TestSiteAccountStoreModule;
import org.example.age.site.provider.userlocalizer.test.TestSiteUserLocalizerModule;
import org.example.age.testing.api.TestModels;
import org.junit.jupiter.api.Test;

public final class SiteVerificationManagerTest {

    private final SiteVerificationManager manager = TestComponent.create();

    @Test
    public void verify() {
        VerificationRequest request = TestModels.createVerificationRequest("site");
        VerificationRequest managerRequest = await(manager.onVerificationRequestReceived("username", request));
        assertThat(managerRequest).isEqualTo(request);

        VerifiedUser user = VerifiedUser.builder()
                .pseudonym(SecureId.fromString("keXeY3kiQDgOhenFw9GMFv3zUFSCSsqrcsmwf3DvpdA"))
                .ageRange(AgeRange.builder().min(18).build())
                .build();
        AgeCertificate ageCertificate =
                AgeCertificate.builder().request(request).user(user).build();
        await(manager.onAgeCertificateReceived(ageCertificate));

        VerificationState state = await(manager.getVerificationState("username"));
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
        VerificationRequest request = TestModels.createVerificationRequest("site");
        await(manager.onVerificationRequestReceived("duplicate", request));

        AgeCertificate ageCertificate = TestModels.createAgeCertificate(request);
        awaitErrorCode(manager.onAgeCertificateReceived(ageCertificate), 409);
    }

    @Test
    public void error_AgeCertificateReceivedTwice() {
        VerificationRequest request = TestModels.createVerificationRequest("site");
        await(manager.onVerificationRequestReceived("username", request));

        AgeCertificate ageCertificate = TestModels.createAgeCertificate(request);
        await(manager.onAgeCertificateReceived(ageCertificate));

        awaitErrorCode(manager.onAgeCertificateReceived(ageCertificate), 404);
    }

    @Test
    public void error_AccountNotFound() {
        AgeCertificate ageCertificate = TestModels.createAgeCertificate();
        awaitErrorCode(manager.onAgeCertificateReceived(ageCertificate), 404);
    }

    /** Dagger component for {@link SiteVerificationManager}. */
    @Component(
            modules = {
                TestSiteAccountStoreModule.class,
                TestPendingStoreModule.class,
                TestSiteUserLocalizerModule.class,
            })
    @Singleton
    interface TestComponent extends Supplier<SiteVerificationManager> {

        static SiteVerificationManager create() {
            SiteEndpointConfig config = SiteEndpointConfig.builder()
                    .id("site")
                    .verifiedAccountExpiresIn(Duration.ofDays(30))
                    .build();
            return DaggerSiteVerificationManagerTest_TestComponent.factory()
                    .create(config)
                    .get();
        }

        @Component.Factory
        interface Factory {

            TestComponent create(@BindsInstance SiteEndpointConfig config);
        }
    }
}
