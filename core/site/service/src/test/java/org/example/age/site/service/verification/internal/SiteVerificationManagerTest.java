package org.example.age.site.service.verification.internal;

import static org.assertj.core.api.Assertions.assertThat;

import dagger.Component;
import dagger.Module;
import dagger.Provides;
import java.security.KeyPair;
import java.security.PublicKey;
import java.time.Duration;
import javax.inject.Named;
import javax.inject.Singleton;
import org.assertj.core.data.Offset;
import org.example.age.common.api.data.AuthMatchData;
import org.example.age.common.api.extractor.builtin.UserAgentAuthMatchData;
import org.example.age.common.service.crypto.internal.AuthMatchDataEncryptor;
import org.example.age.common.service.key.PseudonymKeyProvider;
import org.example.age.common.service.store.inmemory.InMemoryPendingStoreFactoryModule;
import org.example.age.data.certificate.AgeCertificate;
import org.example.age.data.certificate.SignedAgeCertificate;
import org.example.age.data.certificate.VerificationRequest;
import org.example.age.data.certificate.VerificationSession;
import org.example.age.data.crypto.AesGcmEncryptionPackage;
import org.example.age.data.crypto.BytesValue;
import org.example.age.data.crypto.DigitalSignature;
import org.example.age.data.crypto.SecureId;
import org.example.age.data.crypto.SigningKeys;
import org.example.age.data.user.VerifiedUser;
import org.example.age.site.service.config.test.StubSiteConfigModule;
import org.example.age.site.service.data.internal.SiteServiceJsonSerializerModule;
import org.example.age.site.service.store.InMemoryVerificationStoreModule;
import org.example.age.site.service.store.VerificationState;
import org.example.age.site.service.store.VerificationStatus;
import org.example.age.site.service.store.VerificationStore;
import org.example.age.testing.api.StubDispatcher;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public final class SiteVerificationManagerTest {

    private SiteVerificationManager verificationManager;

    private VerificationStore verificationStore;

    private static AuthMatchDataEncryptor authDataEncryptor;
    private static KeyPair avsSigningKeyPair;
    private static SecureId pseudonymKey;

    @BeforeEach
    public void createSiteVerificationManagerEtAl() {
        TestComponent component = TestComponent.create();
        verificationManager = component.siteVerificationManager();
        verificationStore = component.verificationStore();
        authDataEncryptor = component.authMatchDataEncryptor();
    }

    @BeforeAll
    public static void generateKeys() {
        avsSigningKeyPair = SigningKeys.generateEd25519KeyPair();
        pseudonymKey = SecureId.generate();
    }

    @Test
    public void verify() {
        VerificationSession session = createVerificationSession();
        AuthMatchData authData = UserAgentAuthMatchData.of("agent");
        int sessionStatusCode =
                verificationManager.onVerificationSessionReceived("username", authData, session, StubDispatcher.get());
        assertThat(sessionStatusCode).isEqualTo(200);

        VerifiedUser user = createVerifiedUser();
        AuthMatchData remoteAuthData = UserAgentAuthMatchData.of("agent");
        SignedAgeCertificate signedCertificate = createSignedAgeCertificate(session, user, remoteAuthData);
        int certificateStatusCode = verificationManager.onSignedAgeCertificateReceived(signedCertificate);
        assertThat(certificateStatusCode).isEqualTo(200);

        VerificationState state = verificationStore.load("username");
        assertThat(state.status()).isEqualTo(VerificationStatus.VERIFIED);
        VerifiedUser expectedUser = user.localize(pseudonymKey);
        assertThat(state.verifiedUser()).isEqualTo(expectedUser);
        long now = System.currentTimeMillis() / 1000;
        long expectedExpiration = now + Duration.ofDays(30).toSeconds();
        assertThat(state.expiration()).isCloseTo(expectedExpiration, Offset.offset(1L));
    }

    @Test
    public void verifyFailed_DuplicateVerification() {
        VerificationSession session1 = createVerificationSession();
        AuthMatchData authData = UserAgentAuthMatchData.of("agent");
        int session1StatusCode = verificationManager.onVerificationSessionReceived(
                "username1", authData, session1, StubDispatcher.get());
        assertThat(session1StatusCode).isEqualTo(200);

        VerifiedUser user = createVerifiedUser();
        AuthMatchData remoteAuthData = UserAgentAuthMatchData.of("agent");
        SignedAgeCertificate signedCertificate1 = createSignedAgeCertificate(session1, user, remoteAuthData);
        int certificateStatusCode1 = verificationManager.onSignedAgeCertificateReceived(signedCertificate1);
        assertThat(certificateStatusCode1).isEqualTo(200);

        VerificationSession session2 = createVerificationSession();
        int session2StatusCode = verificationManager.onVerificationSessionReceived(
                "username2", authData, session2, StubDispatcher.get());
        assertThat(session2StatusCode).isEqualTo(200);

        SignedAgeCertificate signedCertificate2 = createSignedAgeCertificate(session2, user, remoteAuthData);
        int certificateStatusCode2 = verificationManager.onSignedAgeCertificateReceived(signedCertificate2);
        assertThat(certificateStatusCode2).isEqualTo(409);
    }

    @Test
    public void verifyFailed_AuthenticationFailed() {
        VerificationSession session = createVerificationSession();
        AuthMatchData authData = UserAgentAuthMatchData.of("agent1");
        int sessionStatusCode =
                verificationManager.onVerificationSessionReceived("username", authData, session, StubDispatcher.get());
        assertThat(sessionStatusCode).isEqualTo(200);

        VerifiedUser user = createVerifiedUser();
        AuthMatchData remoteAuthData = UserAgentAuthMatchData.of("agent2");
        SignedAgeCertificate signedCertificate = createSignedAgeCertificate(session, user, remoteAuthData);
        int certificateStatusCode = verificationManager.onSignedAgeCertificateReceived(signedCertificate);
        assertThat(certificateStatusCode).isEqualTo(403);
    }

    @Test
    public void verifyFailed_VerifySignedAgeCertificateFailed_InvalidSignature() {
        VerificationSession session = createVerificationSession();
        VerifiedUser user = createVerifiedUser();
        AuthMatchData remoteAuthData = UserAgentAuthMatchData.of("agent");
        SignedAgeCertificate signedCertificate = createSignedAgeCertificate(session, user, remoteAuthData);
        SignedAgeCertificate forgedCertificate =
                SignedAgeCertificate.of(signedCertificate.ageCertificate(), DigitalSignature.ofBytes(new byte[64]));
        int certificateStatusCode = verificationManager.onSignedAgeCertificateReceived(forgedCertificate);
        assertThat(certificateStatusCode).isEqualTo(401);
    }

    @Test
    public void verifyFailed_VerifySignedAgeCertificateFailed_WrongRecipient() {
        VerificationSession session = createVerificationSession("Other Site", Duration.ofMinutes(5));
        VerifiedUser user = createVerifiedUser();
        AuthMatchData remoteAuthData = UserAgentAuthMatchData.of("agent");
        SignedAgeCertificate signedCertificate = createSignedAgeCertificate(session, user, remoteAuthData);
        int certificateStatusCode = verificationManager.onSignedAgeCertificateReceived(signedCertificate);
        assertThat(certificateStatusCode).isEqualTo(403);
    }

    @Test
    public void verifyFailed_VerifySignedAgeCertificateFailed_Expired() {
        VerificationSession session = createVerificationSession("Site", Duration.ofMinutes(-1));
        VerifiedUser user = createVerifiedUser();
        AuthMatchData remoteAuthData = UserAgentAuthMatchData.of("agent");
        SignedAgeCertificate signedCertificate = createSignedAgeCertificate(session, user, remoteAuthData);
        int certificateStatusCode = verificationManager.onSignedAgeCertificateReceived(signedCertificate);
        assertThat(certificateStatusCode).isEqualTo(410);
    }

    @Test
    public void verifyFailed_PendingVerificationNotFound() {
        VerificationSession session = createVerificationSession();
        VerifiedUser user = createVerifiedUser();
        AuthMatchData remoteAuthData = UserAgentAuthMatchData.of("agent");
        SignedAgeCertificate signedCertificate = createSignedAgeCertificate(session, user, remoteAuthData);
        int certificateStatusCode = verificationManager.onSignedAgeCertificateReceived(signedCertificate);
        assertThat(certificateStatusCode).isEqualTo(404);
    }

    @Test
    public void verifyFailed_DecryptAuthTokenFailed() {
        VerificationSession session = createVerificationSession();
        AuthMatchData authData = UserAgentAuthMatchData.of("agent");
        int sessionStatusCode =
                verificationManager.onVerificationSessionReceived("username", authData, session, StubDispatcher.get());
        assertThat(sessionStatusCode).isEqualTo(200);

        VerifiedUser user = createVerifiedUser();
        AesGcmEncryptionPackage authToken = AesGcmEncryptionPackage.of(BytesValue.empty(), BytesValue.empty());
        SignedAgeCertificate signedCertificate = createSignedAgeCertificate(session, user, authToken);
        int certificateStatusCode = verificationManager.onSignedAgeCertificateReceived(signedCertificate);
        assertThat(certificateStatusCode).isEqualTo(401);
    }

    private static VerificationSession createVerificationSession() {
        return createVerificationSession("Site", Duration.ofMinutes(5));
    }

    private static VerificationSession createVerificationSession(String siteId, Duration expiresIn) {
        VerificationRequest request = VerificationRequest.generateForSite(siteId, expiresIn);
        return VerificationSession.create(request);
    }

    private VerifiedUser createVerifiedUser() {
        return VerifiedUser.of(SecureId.generate(), 18);
    }

    private SignedAgeCertificate createSignedAgeCertificate(
            VerificationSession session, VerifiedUser user, AuthMatchData authData) {
        AesGcmEncryptionPackage authToken = authDataEncryptor.encrypt(authData, session.authKey());
        return createSignedAgeCertificate(session, user, authToken);
    }

    private SignedAgeCertificate createSignedAgeCertificate(
            VerificationSession session, VerifiedUser user, AesGcmEncryptionPackage authToken) {
        VerificationRequest request = session.verificationRequest();
        AgeCertificate certificate = AgeCertificate.of(request, user, authToken);
        return SignedAgeCertificate.sign(certificate, avsSigningKeyPair.getPrivate());
    }

    /** Dagger module that binds dependencies for {@link SiteVerificationManager}. */
    @Module(
            includes = {
                SiteVerificationManagerModule.class,
                InMemoryVerificationStoreModule.class,
                InMemoryPendingStoreFactoryModule.class,
                StubSiteConfigModule.class,
                SiteServiceJsonSerializerModule.class,
            })
    interface TestModule {

        @Provides
        @Named("signing")
        @Singleton
        static PublicKey provideAvsPublicSigningKey() {
            return avsSigningKeyPair.getPublic();
        }

        @Provides
        @Singleton
        static PseudonymKeyProvider providePseudonymKeyProvider() {
            return name -> pseudonymKey;
        }
    }

    /**
     * Dagger component that provides a {@link SiteVerificationManager},
     * and also a {@link VerificationStore} and an {@link AuthMatchDataEncryptor}.
     */
    @Component(modules = TestModule.class)
    @Singleton
    interface TestComponent {

        static TestComponent create() {
            return DaggerSiteVerificationManagerTest_TestComponent.create();
        }

        SiteVerificationManager siteVerificationManager();

        VerificationStore verificationStore();

        AuthMatchDataEncryptor authMatchDataEncryptor();
    }
}
