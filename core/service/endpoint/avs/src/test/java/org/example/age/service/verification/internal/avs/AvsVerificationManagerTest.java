package org.example.age.service.verification.internal.avs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.age.testing.api.HttpOptionalAssert.assertThat;

import java.time.Duration;
import org.assertj.core.data.Offset;
import org.example.age.api.base.HttpOptional;
import org.example.age.api.base.ScheduledExecutor;
import org.example.age.api.def.common.VerificationState;
import org.example.age.api.def.common.VerificationStatus;
import org.example.age.data.certificate.SignedAgeCertificate;
import org.example.age.data.certificate.VerificationRequest;
import org.example.age.data.certificate.VerificationSession;
import org.example.age.data.crypto.SecureId;
import org.example.age.data.user.AgeRange;
import org.example.age.data.user.VerifiedUser;
import org.example.age.module.extractor.common.builtin.DisabledAuthMatchData;
import org.example.age.service.verification.internal.avs.test.TestAvsVerificationComponent;
import org.example.age.service.verification.internal.site.FakeSiteVerificationProcessor;
import org.example.age.service.verification.internal.site.fake.FakeSiteVerificationComponent;
import org.example.age.testing.api.StubScheduledExecutor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public final class AvsVerificationManagerTest {

    private FakeSiteVerificationProcessor fakeSiteVerificationProcessor;

    private static AvsVerificationManager avsVerificationManager;

    private static ScheduledExecutor executor;

    @BeforeEach
    public void createFakeSiteVerificationProcessor() {
        fakeSiteVerificationProcessor = FakeSiteVerificationComponent.createFakeSiteVerificationProcessor();
    }

    @BeforeAll
    public static void createAvsVerificationManagerEtAl() {
        avsVerificationManager = TestAvsVerificationComponent.createAvsVerificationManager();
        executor = StubScheduledExecutor.get();
    }

    @Test
    public void verify() {
        VerificationState avsState = avsVerificationManager.getVerificationState("John Smith");
        assertThat(avsState.status()).isEqualTo(VerificationStatus.VERIFIED);
        VerifiedUser avsUser = avsState.verifiedUser();

        HttpOptional<VerificationSession> maybeSession =
                avsVerificationManager.createVerificationSession("Site", executor);
        assertThat(maybeSession).isPresent();
        VerificationRequest request = maybeSession.get().verificationRequest();
        assertThat(request.siteId()).isEqualTo("Site");
        long now = System.currentTimeMillis() / 1000;
        long expectedExpiration = now + Duration.ofMinutes(5).toSeconds();
        assertThat(request.expiration()).isCloseTo(expectedExpiration, Offset.offset(1L));
        String expectedRedirectPath = String.format("/api/linked-verification-request?request-id=%s", request.id());
        assertThat(request.redirectUrl()).isEqualTo(expectedRedirectPath);
        fakeSiteVerificationProcessor.beginVerification("publius");

        int linkStatusCode = avsVerificationManager.linkVerificationRequest("John Smith", request.id(), executor);
        assertThat(linkStatusCode).isEqualTo(200);

        HttpOptional<SignedAgeCertificate> maybeSignedCertificate =
                avsVerificationManager.createAgeCertificate("John Smith", DisabledAuthMatchData.of());
        assertThat(maybeSignedCertificate).isPresent();
        SignedAgeCertificate signedCertificate = maybeSignedCertificate.get();
        HttpOptional<String> maybeRedirectPath =
                fakeSiteVerificationProcessor.onAgeCertificateReceived(signedCertificate);
        assertThat(maybeRedirectPath).isPresent();

        VerificationState siteState = fakeSiteVerificationProcessor.getVerificationState("publius");
        assertThat(siteState.status()).isEqualTo(VerificationStatus.VERIFIED);
        VerifiedUser siteUser = siteState.verifiedUser();
        assertThat(siteUser.pseudonym()).isNotEqualTo(avsUser.pseudonym());
        assertThat(siteUser.ageRange()).isEqualTo(AgeRange.atOrAbove(18));
    }

    @Test
    public void verifyFailed_SiteNotRegistered() {
        HttpOptional<VerificationSession> maybeSession =
                avsVerificationManager.createVerificationSession("Other Site", executor);
        assertThat(maybeSession).isEmptyWithErrorCode(404);
    }

    @Test
    public void verifyFailed_PersonNotVerified() {
        SecureId requestId = SecureId.generate();
        int linkStatusCode = avsVerificationManager.linkVerificationRequest("Bobby Tables", requestId, executor);
        assertThat(linkStatusCode).isEqualTo(403);

        HttpOptional<SignedAgeCertificate> maybeSignedCertificate =
                avsVerificationManager.createAgeCertificate("Bobby Tables", DisabledAuthMatchData.of());
        assertThat(maybeSignedCertificate).isEmptyWithErrorCode(403);
    }

    @Test
    public void verifyFailed_PendingVerificationNotFound() {
        SecureId requestId = SecureId.generate();
        int linkStatusCode = avsVerificationManager.linkVerificationRequest("John Smith", requestId, executor);
        assertThat(linkStatusCode).isEqualTo(404);

        HttpOptional<SignedAgeCertificate> maybeSignedCertificate =
                avsVerificationManager.createAgeCertificate("John Smith", DisabledAuthMatchData.of());
        assertThat(maybeSignedCertificate).isEmptyWithErrorCode(404);
    }
}
