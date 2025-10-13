package org.example.age.avs.endpoint;

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
import java.util.Map;
import java.util.function.Supplier;
import org.example.age.avs.provider.accountstore.test.TestAvsAccountStoreModule;
import org.example.age.avs.provider.userlocalizer.test.TestAvsUserLocalizerModule;
import org.example.age.common.api.AgeCertificate;
import org.example.age.common.api.AgeRange;
import org.example.age.common.api.AgeThresholds;
import org.example.age.common.api.VerificationRequest;
import org.example.age.common.api.VerifiedUser;
import org.example.age.common.api.crypto.SecureId;
import org.example.age.common.provider.pendingstore.test.TestPendingStoreModule;
import org.junit.jupiter.api.Test;

public final class AvsVerificationManagerTest {

    private final AvsVerificationManager manager = TestComponent.create();

    @Test
    public void verify() {
        VerificationRequest request = await(manager.createVerificationRequest("site"));
        assertThat(request.getSiteId()).isEqualTo("site");
        OffsetDateTime expectedExpiration = OffsetDateTime.now(ZoneOffset.UTC).plus(Duration.ofMinutes(5));
        assertThat(request.getExpiration()).isCloseTo(expectedExpiration, within(1, ChronoUnit.SECONDS));

        await(manager.linkVerificationRequest(request.getId(), "person"));

        AgeCertificate ageCertificate = await(manager.createAgeCertificate("person"));
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
        VerificationRequest request = await(manager.createVerificationRequest("other-site"));

        await(manager.linkVerificationRequest(request.getId(), "person"));

        AgeCertificate ageCertificate = await(manager.createAgeCertificate("person"));
        VerifiedUser expectedUser = VerifiedUser.builder()
                .pseudonym(SecureId.fromString("iaDG-BXou0kKr5gg2j0BJj0RKsa00bVvnpbRCiEism4"))
                .ageRange(AgeRange.builder().min(18).build())
                .build();
        AgeCertificate expectedAgeCertificate =
                AgeCertificate.builder().request(request).user(expectedUser).build();
        assertThat(ageCertificate).isEqualTo(expectedAgeCertificate);
    }

    @Test
    public void error_LinkVerificationRequestTwice() {
        VerificationRequest request = await(manager.createVerificationRequest("site"));
        assertThat(request.getSiteId()).isEqualTo("site");

        await(manager.linkVerificationRequest(request.getId(), "person"));

        awaitErrorCode(manager.linkVerificationRequest(request.getId(), "person"), 404);
    }

    @Test
    public void error_CreateAgeCertificateTwice() {
        VerificationRequest request = await(manager.createVerificationRequest("site"));
        assertThat(request.getSiteId()).isEqualTo("site");

        await(manager.linkVerificationRequest(request.getId(), "person"));

        await(manager.createAgeCertificate("person"));

        awaitErrorCode(manager.createAgeCertificate("person"), 404);
    }

    @Test
    public void error_UnverifiedAccount() {
        awaitErrorCode(manager.linkVerificationRequest(SecureId.generate(), "unverified-person"), 403);

        awaitErrorCode(manager.createAgeCertificate("unverified-person"), 403);
    }

    @Test
    public void error_VerificationRequestNotFound() {
        awaitErrorCode(manager.linkVerificationRequest(SecureId.generate(), "person"), 404);

        awaitErrorCode(manager.createAgeCertificate("person"), 404);
    }

    /** Dagger component for {@link AvsVerificationManager}. */
    @Component(
            modules = {
                TestAvsAccountStoreModule.class,
                TestPendingStoreModule.class,
                TestAvsUserLocalizerModule.class,
            })
    @Singleton
    interface TestComponent extends Supplier<AvsVerificationManager> {

        static AvsVerificationManager create() {
            AvsEndpointConfig config = AvsEndpointConfig.builder()
                    .verificationRequestExpiresIn(Duration.ofMinutes(5))
                    .ageThresholds(Map.of("site", AgeThresholds.of(18), "other-site", AgeThresholds.of(18)))
                    .build();
            return DaggerAvsVerificationManagerTest_TestComponent.factory()
                    .create(config)
                    .get();
        }

        @Component.Factory
        interface Factory {

            TestComponent create(@BindsInstance AvsEndpointConfig config);
        }
    }
}
