package org.example.age.service.site.verification.internal;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import org.assertj.core.data.Offset;
import org.example.age.api.common.VerificationState;
import org.example.age.api.common.VerificationStatus;
import org.example.age.data.certificate.SignedAgeCertificate;
import org.example.age.data.certificate.VerificationSession;
import org.example.age.data.crypto.AesGcmEncryptionPackage;
import org.example.age.data.crypto.DigitalSignature;
import org.example.age.module.extractor.common.builtin.UserAgentAuthMatchData;
import org.example.age.service.avs.verification.internal.FakeAvsVerificationFactory;
import org.example.age.service.avs.verification.internal.test.FakeAvsVerificationComponent;
import org.example.age.service.site.verification.internal.test.TestSiteVerificationComponent;
import org.example.age.testing.api.StubDispatcher;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public final class SiteVerificationManagerTest {

    private SiteVerificationManager siteVerificationManager;

    private static FakeAvsVerificationFactory fakeAvsVerificationFactory;

    @BeforeEach
    public void createSiteVerificationManagerEtAl() {
        siteVerificationManager = TestSiteVerificationComponent.createSiteVerificationManager();
    }

    @BeforeAll
    public static void createFakeAvsVerificationFactory() {
        fakeAvsVerificationFactory = FakeAvsVerificationComponent.createFakeAvsVerificationFactory();
    }

    @Test
    public void verify() {
        VerificationSession session = fakeAvsVerificationFactory.createVerificationSession("Site");
        int sessionStatusCode = siteVerificationManager.onVerificationSessionReceived(
                "publius", UserAgentAuthMatchData.of("agent"), session, StubDispatcher.get());
        assertThat(sessionStatusCode).isEqualTo(200);

        SignedAgeCertificate signedCertificate = fakeAvsVerificationFactory.createSignedAgeCertificate(
                "John Smith", UserAgentAuthMatchData.of("agent"), session);
        int certificateStatusCode = siteVerificationManager.onSignedAgeCertificateReceived(signedCertificate);
        assertThat(certificateStatusCode).isEqualTo(200);

        VerificationState state = siteVerificationManager.getVerificationState("publius");
        assertThat(state.status()).isEqualTo(VerificationStatus.VERIFIED);
        assertThat(state.verifiedUser())
                .isNotEqualTo(signedCertificate.ageCertificate().verifiedUser());
        long now = System.currentTimeMillis() / 1000;
        long expectedExpiration = now + Duration.ofDays(30).toSeconds();
        assertThat(state.expiration()).isCloseTo(expectedExpiration, Offset.offset(1L));
    }

    @Test
    public void verifyFailed_DuplicateVerification() {
        VerificationSession session1 = fakeAvsVerificationFactory.createVerificationSession("Site");
        int sessionStatusCode1 = siteVerificationManager.onVerificationSessionReceived(
                "publius", UserAgentAuthMatchData.of("agent"), session1, StubDispatcher.get());
        assertThat(sessionStatusCode1).isEqualTo(200);

        SignedAgeCertificate signedCertificate1 = fakeAvsVerificationFactory.createSignedAgeCertificate(
                "John Smith", UserAgentAuthMatchData.of("agent"), session1);
        int certificateStatusCode1 = siteVerificationManager.onSignedAgeCertificateReceived(signedCertificate1);
        assertThat(certificateStatusCode1).isEqualTo(200);

        VerificationSession session2 = fakeAvsVerificationFactory.createVerificationSession("Site");
        int sessionStatusCode2 = siteVerificationManager.onVerificationSessionReceived(
                "drop-table", UserAgentAuthMatchData.of("agent"), session2, StubDispatcher.get());
        assertThat(sessionStatusCode2).isEqualTo(200);

        SignedAgeCertificate signedCertificate2 = fakeAvsVerificationFactory.createSignedAgeCertificate(
                "John Smith", UserAgentAuthMatchData.of("agent"), session2);
        int certificateStatusCode2 = siteVerificationManager.onSignedAgeCertificateReceived(signedCertificate2);
        assertThat(certificateStatusCode2).isEqualTo(409);
    }

    @Test
    public void verifyFailed_AuthenticationFailed() {
        VerificationSession session = fakeAvsVerificationFactory.createVerificationSession("Site");
        int sessionStatusCode = siteVerificationManager.onVerificationSessionReceived(
                "drop-table", UserAgentAuthMatchData.of("agent1"), session, StubDispatcher.get());
        assertThat(sessionStatusCode).isEqualTo(200);

        SignedAgeCertificate signedCertificate = fakeAvsVerificationFactory.createSignedAgeCertificate(
                "John Smith", UserAgentAuthMatchData.of("agent2"), session);
        int certificateStatusCode = siteVerificationManager.onSignedAgeCertificateReceived(signedCertificate);
        assertThat(certificateStatusCode).isEqualTo(403);
    }

    @Test
    public void verifyFailed_VerifySignedAgeCertificateFailed_InvalidSignature() {
        VerificationSession session = fakeAvsVerificationFactory.createVerificationSession("Site");
        SignedAgeCertificate signedCertificate = fakeAvsVerificationFactory.createSignedAgeCertificate(
                "John Smith", UserAgentAuthMatchData.of("agent"), session);
        SignedAgeCertificate forgedCertificate = forgeSignedAgeCertificate(signedCertificate);
        int certificateStatusCode = siteVerificationManager.onSignedAgeCertificateReceived(forgedCertificate);
        assertThat(certificateStatusCode).isEqualTo(401);
    }

    @Test
    public void verifyFailed_VerifySignedAgeCertificateFailed_WrongRecipient() {
        VerificationSession session = fakeAvsVerificationFactory.createVerificationSession("Other Site");
        SignedAgeCertificate signedCertificate = fakeAvsVerificationFactory.createSignedAgeCertificate(
                "John Smith", UserAgentAuthMatchData.of("agent"), session);
        int certificateStatusCode = siteVerificationManager.onSignedAgeCertificateReceived(signedCertificate);
        assertThat(certificateStatusCode).isEqualTo(403);
    }

    @Test
    public void verifyFailed_VerifySignedAgeCertificateFailed_Expired() {
        VerificationSession session =
                fakeAvsVerificationFactory.createVerificationSession("Site", Duration.ofMinutes(-1));
        SignedAgeCertificate signedCertificate = fakeAvsVerificationFactory.createSignedAgeCertificate(
                "John Smith", UserAgentAuthMatchData.of("agent"), session);
        int certificateStatusCode = siteVerificationManager.onSignedAgeCertificateReceived(signedCertificate);
        assertThat(certificateStatusCode).isEqualTo(410);
    }

    @Test
    public void verifyFailed_PendingVerificationNotFound() {
        VerificationSession session = fakeAvsVerificationFactory.createVerificationSession("Site");
        SignedAgeCertificate signedCertificate = fakeAvsVerificationFactory.createSignedAgeCertificate(
                "John Smith", UserAgentAuthMatchData.of("agent"), session);
        int certificateStatusCode = siteVerificationManager.onSignedAgeCertificateReceived(signedCertificate);
        assertThat(certificateStatusCode).isEqualTo(404);
    }

    @Test
    public void verifyFailed_DecryptAuthTokenFailed() {
        VerificationSession session = fakeAvsVerificationFactory.createVerificationSession("Site");
        int sessionStatusCode = siteVerificationManager.onVerificationSessionReceived(
                "publius", UserAgentAuthMatchData.of("agent"), session, StubDispatcher.get());
        assertThat(sessionStatusCode).isEqualTo(200);

        AesGcmEncryptionPackage authToken = AesGcmEncryptionPackage.empty();
        SignedAgeCertificate signedCertificate =
                fakeAvsVerificationFactory.createSignedAgeCertificate("John Smith", authToken, session);
        int certificateStatusCode = siteVerificationManager.onSignedAgeCertificateReceived(signedCertificate);
        assertThat(certificateStatusCode).isEqualTo(401);
    }

    private static SignedAgeCertificate forgeSignedAgeCertificate(SignedAgeCertificate signedCertificate) {
        DigitalSignature forgedSignature = DigitalSignature.ofBytes(new byte[32]);
        return SignedAgeCertificate.of(signedCertificate.ageCertificate(), forgedSignature);
    }
}
