package org.example.age.service.verification.internal.site;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.age.testing.api.HttpOptionalAssert.assertThat;

import java.time.Duration;
import org.assertj.core.data.Offset;
import org.example.age.api.base.HttpOptional;
import org.example.age.api.base.ScheduledExecutor;
import org.example.age.api.def.common.VerificationState;
import org.example.age.api.def.common.VerificationStatus;
import org.example.age.data.certificate.SignedAgeCertificate;
import org.example.age.data.certificate.VerificationSession;
import org.example.age.data.crypto.AesGcmEncryptionPackage;
import org.example.age.data.crypto.DigitalSignature;
import org.example.age.data.user.VerifiedUser;
import org.example.age.module.extractor.common.builtin.UserAgentAuthMatchData;
import org.example.age.service.verification.internal.avs.FakeAvsVerificationFactory;
import org.example.age.service.verification.internal.avs.fake.FakeAvsVerificationComponent;
import org.example.age.service.verification.internal.site.test.TestSiteVerificationComponent;
import org.example.age.testing.api.StubScheduledExecutor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public final class SiteVerificationManagerTest {

    private SiteVerificationManager siteVerificationManager;

    private static FakeAvsVerificationFactory fakeAvsVerificationFactory;

    private static ScheduledExecutor executor;

    @BeforeEach
    public void createSiteVerificationManager() {
        siteVerificationManager = TestSiteVerificationComponent.createSiteVerificationManager();
    }

    @BeforeAll
    public static void createFakeAvsVerificationFactoryEtAl() {
        fakeAvsVerificationFactory = FakeAvsVerificationComponent.createFakeAvsVerificationFactory();
        executor = StubScheduledExecutor.get();
    }

    @Test
    public void verify() {
        VerificationState avsState = fakeAvsVerificationFactory.getVerificationState("John Smith");
        assertThat(avsState.status()).isEqualTo(VerificationStatus.VERIFIED);
        VerifiedUser avsUser = avsState.verifiedUser();

        VerificationSession session = fakeAvsVerificationFactory.createVerificationSession("Site");
        int sessionStatusCode = siteVerificationManager.onVerificationSessionReceived(
                "publius", UserAgentAuthMatchData.of("agent"), session, executor);
        assertThat(sessionStatusCode).isEqualTo(200);

        SignedAgeCertificate signedCertificate = fakeAvsVerificationFactory.createSignedAgeCertificate(
                "John Smith", UserAgentAuthMatchData.of("agent"), session);
        HttpOptional<String> maybeRedirectPath = siteVerificationManager.onAgeCertificateReceived(signedCertificate);
        assertThat(maybeRedirectPath).hasValue("/api/verification-state");

        VerificationState siteState = siteVerificationManager.getVerificationState("publius");
        assertThat(siteState.status()).isEqualTo(VerificationStatus.VERIFIED);
        VerifiedUser siteUser = siteState.verifiedUser();
        assertThat(siteUser.pseudonym()).isNotEqualTo(avsUser.pseudonym());
        long now = System.currentTimeMillis() / 1000;
        long expectedExpiration = now + Duration.ofDays(30).toSeconds();
        assertThat(siteState.expiration()).isCloseTo(expectedExpiration, Offset.offset(1L));
    }

    @Test
    public void verifyFailed_DuplicateVerification() {
        VerificationSession session1 = fakeAvsVerificationFactory.createVerificationSession("Site");
        int sessionStatusCode1 = siteVerificationManager.onVerificationSessionReceived(
                "publius", UserAgentAuthMatchData.of("agent"), session1, executor);
        assertThat(sessionStatusCode1).isEqualTo(200);

        SignedAgeCertificate signedCertificate1 = fakeAvsVerificationFactory.createSignedAgeCertificate(
                "John Smith", UserAgentAuthMatchData.of("agent"), session1);
        HttpOptional<String> maybeRedirectPath1 = siteVerificationManager.onAgeCertificateReceived(signedCertificate1);
        assertThat(maybeRedirectPath1).isPresent();

        VerificationSession session2 = fakeAvsVerificationFactory.createVerificationSession("Site");
        int sessionStatusCode2 = siteVerificationManager.onVerificationSessionReceived(
                "drop-table", UserAgentAuthMatchData.of("agent"), session2, executor);
        assertThat(sessionStatusCode2).isEqualTo(200);

        SignedAgeCertificate signedCertificate2 = fakeAvsVerificationFactory.createSignedAgeCertificate(
                "John Smith", UserAgentAuthMatchData.of("agent"), session2);
        HttpOptional<String> maybeRedirectPath2 = siteVerificationManager.onAgeCertificateReceived(signedCertificate2);
        assertThat(maybeRedirectPath2).isEmptyWithErrorCode(409);
    }

    @Test
    public void verifyFailed_AuthenticationFailed() {
        VerificationSession session = fakeAvsVerificationFactory.createVerificationSession("Site");
        int sessionStatusCode = siteVerificationManager.onVerificationSessionReceived(
                "drop-table", UserAgentAuthMatchData.of("agent1"), session, executor);
        assertThat(sessionStatusCode).isEqualTo(200);

        SignedAgeCertificate signedCertificate = fakeAvsVerificationFactory.createSignedAgeCertificate(
                "John Smith", UserAgentAuthMatchData.of("agent2"), session);
        HttpOptional<String> maybeRedirectPath = siteVerificationManager.onAgeCertificateReceived(signedCertificate);
        assertThat(maybeRedirectPath).isEmptyWithErrorCode(403);
    }

    @Test
    public void verifyFailed_VerifySignedAgeCertificateFailed_InvalidSignature() {
        VerificationSession session = fakeAvsVerificationFactory.createVerificationSession("Site");
        SignedAgeCertificate signedCertificate = fakeAvsVerificationFactory.createSignedAgeCertificate(
                "John Smith", UserAgentAuthMatchData.of("agent"), session);
        SignedAgeCertificate forgedCertificate = forgeSignedAgeCertificate(signedCertificate);
        HttpOptional<String> maybeRedirectPath = siteVerificationManager.onAgeCertificateReceived(forgedCertificate);
        assertThat(maybeRedirectPath).isEmptyWithErrorCode(401);
    }

    @Test
    public void verifyFailed_VerifySignedAgeCertificateFailed_WrongRecipient() {
        VerificationSession session = fakeAvsVerificationFactory.createVerificationSession("Other Site");
        SignedAgeCertificate signedCertificate = fakeAvsVerificationFactory.createSignedAgeCertificate(
                "John Smith", UserAgentAuthMatchData.of("agent"), session);
        HttpOptional<String> maybeRedirectPath = siteVerificationManager.onAgeCertificateReceived(signedCertificate);
        assertThat(maybeRedirectPath).isEmptyWithErrorCode(403);
    }

    @Test
    public void verifyFailed_VerifySignedAgeCertificateFailed_Expired() {
        VerificationSession session =
                fakeAvsVerificationFactory.createVerificationSession("Site", Duration.ofMinutes(-1));
        SignedAgeCertificate signedCertificate = fakeAvsVerificationFactory.createSignedAgeCertificate(
                "John Smith", UserAgentAuthMatchData.of("agent"), session);
        HttpOptional<String> maybeRedirectPath = siteVerificationManager.onAgeCertificateReceived(signedCertificate);
        assertThat(maybeRedirectPath).isEmptyWithErrorCode(410);
    }

    @Test
    public void verifyFailed_PendingVerificationNotFound() {
        VerificationSession session = fakeAvsVerificationFactory.createVerificationSession("Site");
        SignedAgeCertificate signedCertificate = fakeAvsVerificationFactory.createSignedAgeCertificate(
                "John Smith", UserAgentAuthMatchData.of("agent"), session);
        HttpOptional<String> maybeRedirectPath = siteVerificationManager.onAgeCertificateReceived(signedCertificate);
        assertThat(maybeRedirectPath).isEmptyWithErrorCode(404);
    }

    @Test
    public void verifyFailed_DecryptAuthTokenFailed() {
        VerificationSession session = fakeAvsVerificationFactory.createVerificationSession("Site");
        int sessionStatusCode = siteVerificationManager.onVerificationSessionReceived(
                "publius", UserAgentAuthMatchData.of("agent"), session, executor);
        assertThat(sessionStatusCode).isEqualTo(200);

        AesGcmEncryptionPackage authToken = AesGcmEncryptionPackage.empty();
        SignedAgeCertificate signedCertificate =
                fakeAvsVerificationFactory.createSignedAgeCertificate("John Smith", authToken, session);
        HttpOptional<String> maybeRedirectPath = siteVerificationManager.onAgeCertificateReceived(signedCertificate);
        assertThat(maybeRedirectPath).isEmptyWithErrorCode(401);
    }

    private static SignedAgeCertificate forgeSignedAgeCertificate(SignedAgeCertificate signedCertificate) {
        DigitalSignature forgedSignature = DigitalSignature.ofBytes(new byte[32]);
        return SignedAgeCertificate.of(signedCertificate.ageCertificate(), forgedSignature);
    }
}
