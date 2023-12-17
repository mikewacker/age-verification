package org.example.age.service.verification.internal.site;

import com.fasterxml.jackson.core.type.TypeReference;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.example.age.api.base.Dispatcher;
import org.example.age.api.base.HttpOptional;
import org.example.age.api.def.common.AuthMatchData;
import org.example.age.api.def.common.VerificationState;
import org.example.age.data.certificate.AgeCertificate;
import org.example.age.data.certificate.SignedAgeCertificate;
import org.example.age.data.certificate.VerificationRequest;
import org.example.age.data.certificate.VerificationSession;
import org.example.age.data.crypto.Aes256Key;
import org.example.age.data.crypto.AesGcmEncryptionPackage;
import org.example.age.data.crypto.SecureId;
import org.example.age.data.user.VerifiedUser;
import org.example.age.service.crypto.internal.common.AgeCertificateVerifier;
import org.example.age.service.crypto.internal.common.AuthMatchDataEncryptor;
import org.example.age.service.crypto.internal.common.VerifiedUserLocalizer;
import org.example.age.service.module.config.site.RefreshableSiteConfigProvider;
import org.example.age.service.store.common.PendingStore;
import org.example.age.service.store.common.PendingStoreFactory;
import org.example.age.service.store.common.VerificationStore;

@Singleton
final class SiteVerificationManagerImpl implements SiteVerificationManager {

    private static final String PENDING_VERIFICATION_STORE_NAME = "verification";
    private static final String PSEUDONYM_KEY_NAME = "local";

    private final VerificationStore verificationStore;
    private final PendingStoreFactory pendingStoreFactory;
    private final AgeCertificateVerifier certificateVerifier;
    private final VerifiedUserLocalizer userLocalizer;
    private final AuthMatchDataEncryptor authDataEncryptor;
    private final RefreshableSiteConfigProvider siteConfigProvider;

    @Inject
    public SiteVerificationManagerImpl(
            VerificationStore verificationStore,
            PendingStoreFactory pendingStoreFactory,
            AgeCertificateVerifier certificateVerifier,
            VerifiedUserLocalizer userLocalizer,
            AuthMatchDataEncryptor authDataEncryptor,
            RefreshableSiteConfigProvider siteConfigProvider) {
        this.verificationStore = verificationStore;
        this.pendingStoreFactory = pendingStoreFactory;
        this.certificateVerifier = certificateVerifier;
        this.userLocalizer = userLocalizer;
        this.authDataEncryptor = authDataEncryptor;
        this.siteConfigProvider = siteConfigProvider;
    }

    @Override
    public VerificationState getVerificationState(String accountId) {
        return verificationStore.load(accountId);
    }

    @Override
    public int onVerificationSessionReceived(
            String accountId, AuthMatchData authData, VerificationSession session, Dispatcher dispatcher) {
        Verification pendingVerification = new Verification(accountId, authData, session);
        putPendingVerification(pendingVerification, dispatcher);
        return 200;
    }

    @Override
    public int onAgeCertificateReceived(SignedAgeCertificate signedCertificate) {
        int verifyStatusCode = verifySignedAgeCertificate(signedCertificate);
        if (verifyStatusCode != 200) {
            return verifyStatusCode;
        }

        AgeCertificate certificate = signedCertificate.ageCertificate();
        Optional<Verification> maybePendingVerification =
                tryRemovePendingVerification(certificate.verificationRequest().id());
        if (maybePendingVerification.isEmpty()) {
            return 404;
        }
        Verification pendingVerification = maybePendingVerification.get();

        int authStatusCode = authenticate(
                pendingVerification.authData(),
                certificate.authToken(),
                pendingVerification.verificationSession().authKey());
        if (authStatusCode != 200) {
            return authStatusCode;
        }

        VerifiedUser localUser = userLocalizer.localize(certificate.verifiedUser(), PSEUDONYM_KEY_NAME);
        return trySaveUser(pendingVerification.accountId(), localUser);
    }

    /** Puts a pending verification. */
    private void putPendingVerification(Verification pendingVerification, Dispatcher dispatcher) {
        PendingStore<Verification> pendingVerifications =
                pendingStoreFactory.getOrCreate(PENDING_VERIFICATION_STORE_NAME, new TypeReference<>() {});
        VerificationRequest request = pendingVerification.verificationSession().verificationRequest();
        SecureId requestId = request.id();
        long expiration = request.expiration();
        pendingVerifications.put(requestId.toString(), pendingVerification, expiration, dispatcher.getIoThread());
    }

    /** Verifies a {@link SignedAgeCertificate}, returning a status code. */
    private int verifySignedAgeCertificate(SignedAgeCertificate signedCertificate) {
        if (!certificateVerifier.verify(signedCertificate)) {
            return 401;
        }

        VerificationRequest request = signedCertificate.ageCertificate().verificationRequest();
        String siteId = siteConfigProvider.get().id();
        if (!request.isIntendedRecipient(siteId)) {
            return 403;
        }

        if (request.isExpired()) {
            return 410;
        }

        return 200;
    }

    /** Removes and returns the pending verification for the request ID, if present. */
    private Optional<Verification> tryRemovePendingVerification(SecureId requestId) {
        PendingStore<Verification> pendingVerifications =
                pendingStoreFactory.getOrCreate(PENDING_VERIFICATION_STORE_NAME, new TypeReference<>() {});
        return pendingVerifications.tryRemove(requestId.toString());
    }

    /** Runs an authentication check, returning a status code. */
    private int authenticate(AuthMatchData authData, AesGcmEncryptionPackage remoteAuthToken, Aes256Key key) {
        HttpOptional<AuthMatchData> maybeRemoteAuthData = authDataEncryptor.tryDecrypt(remoteAuthToken, key);
        if (maybeRemoteAuthData.isEmpty()) {
            return maybeRemoteAuthData.statusCode();
        }
        AuthMatchData remoteAuthData = maybeRemoteAuthData.get();

        return authData.match(remoteAuthData) ? 200 : 403;
    }

    /** Saves a {@link VerifiedUser} for the account, returning a status code. */
    private int trySaveUser(String accountId, VerifiedUser user) {
        long now = System.currentTimeMillis() / 1000;
        long expiresIn = siteConfigProvider.get().verifiedAccountExpiresIn();
        long expiration = now + expiresIn;
        VerificationState state = VerificationState.verified(user, expiration);
        Optional<String> maybeConflictingAccountId = verificationStore.trySave(accountId, state);
        return maybeConflictingAccountId.isEmpty() ? 200 : 409;
    }

    /** Pending verification. */
    public record Verification(String accountId, AuthMatchData authData, VerificationSession verificationSession) {}
}
