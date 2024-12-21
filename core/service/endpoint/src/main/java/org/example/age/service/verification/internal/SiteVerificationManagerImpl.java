package org.example.age.service.verification.internal;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.mikewacker.drift.api.HttpOptional;
import io.github.mikewacker.drift.api.ScheduledExecutor;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.Optional;
import org.example.age.api.def.AuthMatchData;
import org.example.age.api.def.VerificationState;
import org.example.age.data.certificate.AgeCertificate;
import org.example.age.data.certificate.SignedAgeCertificate;
import org.example.age.data.certificate.VerificationRequest;
import org.example.age.data.certificate.VerificationSession;
import org.example.age.data.crypto.Aes256Key;
import org.example.age.data.crypto.AesGcmEncryptionPackage;
import org.example.age.data.crypto.SecureId;
import org.example.age.data.user.VerifiedUser;
import org.example.age.service.config.RefreshableSiteConfigProvider;
import org.example.age.service.crypto.internal.AgeCertificateVerifier;
import org.example.age.service.crypto.internal.AuthMatchDataEncryptor;
import org.example.age.service.crypto.internal.VerifiedUserLocalizer;
import org.example.age.service.store.PendingStore;
import org.example.age.service.store.PendingStoreFactory;
import org.example.age.service.store.VerificationStore;

@Singleton
final class SiteVerificationManagerImpl implements SiteVerificationManager {

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
            String accountId, AuthMatchData authData, VerificationSession session, ScheduledExecutor executor) {
        Verification pendingVerification = new Verification(accountId, authData, session);
        putPendingVerification(pendingVerification, executor);
        return 200;
    }

    @Override
    public HttpOptional<String> onAgeCertificateReceived(SignedAgeCertificate signedCertificate) {
        int verifyStatusCode = verifySignedAgeCertificate(signedCertificate);
        if (verifyStatusCode != 200) {
            return HttpOptional.empty(verifyStatusCode);
        }

        AgeCertificate certificate = signedCertificate.ageCertificate();
        HttpOptional<Verification> maybePendingVerification =
                tryRemovePendingVerification(certificate.verificationRequest().id());
        if (maybePendingVerification.isEmpty()) {
            return maybePendingVerification.convertEmpty();
        }
        Verification pendingVerification = maybePendingVerification.get();

        int authStatusCode = authenticate(
                pendingVerification.authData(),
                certificate.authToken(),
                pendingVerification.verificationSession().authKey());
        if (authStatusCode != 200) {
            return HttpOptional.empty(authStatusCode);
        }

        VerifiedUser localUser = userLocalizer.localize(certificate.verifiedUser(), "local");
        int saveStatusCode = trySaveUser(pendingVerification.accountId(), localUser);
        if (saveStatusCode != 200) {
            return HttpOptional.empty(saveStatusCode);
        }

        String redirectPath = siteConfigProvider.get().redirectPath();
        return HttpOptional.of(redirectPath);
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

    /** Runs an authentication check, returning a 403 error if the check fails. */
    private int authenticate(AuthMatchData authData, AesGcmEncryptionPackage remoteAuthToken, Aes256Key authKey) {
        HttpOptional<AuthMatchData> maybeRemoteAuthData = authDataEncryptor.tryDecrypt(remoteAuthToken, authKey);
        if (maybeRemoteAuthData.isEmpty()) {
            return maybeRemoteAuthData.statusCode();
        }
        AuthMatchData remoteAuthData = maybeRemoteAuthData.get();

        return authData.match(remoteAuthData) ? 200 : 403;
    }

    /** Saves a {@link VerifiedUser} for the account, returning a 409 error if a duplicate verification occurs. */
    private int trySaveUser(String accountId, VerifiedUser user) {
        long now = System.currentTimeMillis() / 1000;
        long expiresIn = siteConfigProvider.get().verifiedAccountExpiresIn();
        long expiration = now + expiresIn;
        VerificationState state = VerificationState.verified(user, expiration);
        Optional<String> maybeConflictingAccountId = verificationStore.trySave(accountId, state);
        return maybeConflictingAccountId.isEmpty() ? 200 : 409;
    }

    /** Puts a pending verification. */
    private void putPendingVerification(Verification pendingVerification, ScheduledExecutor executor) {
        PendingStore<Verification> pendingVerifications = getPendingVerifications();
        VerificationRequest request = pendingVerification.verificationSession().verificationRequest();
        pendingVerifications.put(request.id().toString(), pendingVerification, request.expiration(), executor);
    }

    /** Removes and returns the pending verification for the request ID, or returns a 404 error. */
    private HttpOptional<Verification> tryRemovePendingVerification(SecureId requestId) {
        PendingStore<Verification> pendingVerifications = getPendingVerifications();
        Optional<Verification> maybePendingVerification = pendingVerifications.tryRemove(requestId.toString());
        return HttpOptional.fromOptional(maybePendingVerification, 404);
    }

    /** Gets the store for pending verifications. */
    private PendingStore<Verification> getPendingVerifications() {
        return pendingStoreFactory.getOrCreate("verification", new TypeReference<>() {});
    }

    /** Pending verification. */
    public record Verification(String accountId, AuthMatchData authData, VerificationSession verificationSession) {}
}
