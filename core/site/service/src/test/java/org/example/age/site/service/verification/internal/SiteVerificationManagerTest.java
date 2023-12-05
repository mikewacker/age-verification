package org.example.age.site.service.verification.internal;

import static org.assertj.core.api.Assertions.assertThat;

import dagger.Component;
import java.time.Duration;
import javax.inject.Singleton;
import org.assertj.core.data.Offset;
import org.example.age.avs.service.verification.internal.FakeAvsVerificationFactory;
import org.example.age.avs.service.verification.internal.test.TestAvsVerificationFactoryModule;
import org.example.age.common.api.data.VerificationState;
import org.example.age.common.api.data.VerificationStatus;
import org.example.age.common.api.extractor.builtin.UserAgentAuthMatchData;
import org.example.age.data.certificate.SignedAgeCertificate;
import org.example.age.data.certificate.VerificationSession;
import org.example.age.data.crypto.AesGcmEncryptionPackage;
import org.example.age.data.crypto.BytesValue;
import org.example.age.data.crypto.DigitalSignature;
import org.example.age.site.service.store.VerificationStore;
import org.example.age.site.service.verification.internal.test.TestSiteVerificationManagerModule;
import org.example.age.testing.api.StubDispatcher;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public final class SiteVerificationManagerTest {

    private SiteVerificationManager siteVerificationManager;
    private VerificationStore siteVerificationStore;

    private static FakeAvsVerificationFactory fakeAvsVerificationFactory;

    @BeforeEach
    public void createSiteVerificationManagerEtAl() {
        TestComponent component = TestComponent.create();
        siteVerificationManager = component.siteVerificationManager();
        siteVerificationStore = component.verificationStore();
    }

    @BeforeAll
    public static void createFakeAvsVerificationFactory() {
        fakeAvsVerificationFactory = FakeAvsComponent.createFakeAvsVerificationFactory();
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

        VerificationState state = siteVerificationStore.load("publius");
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

        AesGcmEncryptionPackage authToken = AesGcmEncryptionPackage.of(BytesValue.empty(), BytesValue.empty());
        SignedAgeCertificate signedCertificate =
                fakeAvsVerificationFactory.createSignedAgeCertificate("John Smith", authToken, session);
        int certificateStatusCode = siteVerificationManager.onSignedAgeCertificateReceived(signedCertificate);
        assertThat(certificateStatusCode).isEqualTo(401);
    }

    private static SignedAgeCertificate forgeSignedAgeCertificate(SignedAgeCertificate signedCertificate) {
        DigitalSignature forgedSignature = DigitalSignature.ofBytes(new byte[32]);
        return SignedAgeCertificate.of(signedCertificate.ageCertificate(), forgedSignature);
    }

    /** Dagger component that provides a {@link SiteVerificationManager}, and also a {@link VerificationStore}. */
    @Component(modules = TestSiteVerificationManagerModule.class)
    @Singleton
    interface TestComponent {

        static TestComponent create() {
            return DaggerSiteVerificationManagerTest_TestComponent.create();
        }

        SiteVerificationManager siteVerificationManager();

        VerificationStore verificationStore();
    }

    /** Dagger component that provides a {@link FakeAvsVerificationFactory}. */
    @Component(modules = TestAvsVerificationFactoryModule.class)
    @Singleton
    interface FakeAvsComponent {

        static FakeAvsVerificationFactory createFakeAvsVerificationFactory() {
            FakeAvsComponent component = DaggerSiteVerificationManagerTest_FakeAvsComponent.create();
            return component.fakeAvsVerificationFactory();
        }

        FakeAvsVerificationFactory fakeAvsVerificationFactory();
    }
}
