package org.example.age.site.service.verification.internal;

import com.fasterxml.jackson.core.type.TypeReference;
import java.security.PublicKey;
import java.time.Duration;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import org.example.age.api.Dispatcher;
import org.example.age.api.HttpOptional;
import org.example.age.common.api.data.AuthMatchData;
import org.example.age.common.api.data.AuthMatchDataExtractor;
import org.example.age.common.service.store.PendingStore;
import org.example.age.common.service.store.PendingStoreFactory;
import org.example.age.data.certificate.AgeCertificate;
import org.example.age.data.certificate.SignedAgeCertificate;
import org.example.age.data.certificate.VerificationRequest;
import org.example.age.data.certificate.VerificationSession;
import org.example.age.data.crypto.Aes256Key;
import org.example.age.data.crypto.AesGcmEncryptionPackage;
import org.example.age.data.crypto.SecureId;
import org.example.age.data.user.VerifiedUser;
import org.example.age.site.service.store.VerificationState;
import org.example.age.site.service.store.VerificationStore;

@Singleton
final class VerificationManagerImpl implements VerificationManager {

    private static final String VERIFICATION_STORE_NAME = "verification";

    private final AuthMatchDataExtractor authDataExtractor;
    private final VerificationStore verificationStore;
    private final PendingStoreFactory pendingStoreFactory;
    private final Provider<PublicKey> avsPublicSigningKeyProvider;
    private final Provider<String> siteIdProvider;
    private final Provider<SecureId> pseudonymKeyProvider;
    private final Provider<Duration> expiresInProvider;

    @Inject
    public VerificationManagerImpl(
            AuthMatchDataExtractor authDataExtractor,
            VerificationStore verificationStore,
            PendingStoreFactory pendingStoreFactory,
            @Named("avsSigning") Provider<PublicKey> avsPublicSigningKeyProvider,
            @Named("siteId") Provider<String> siteIdProvider,
            @Named("pseudonymKey") Provider<SecureId> pseudonymKeyProvider,
            @Named("expiresIn") Provider<Duration> expiresInProvider) {
        this.authDataExtractor = authDataExtractor;
        this.verificationStore = verificationStore;
        this.pendingStoreFactory = pendingStoreFactory;
        this.avsPublicSigningKeyProvider = avsPublicSigningKeyProvider;
        this.siteIdProvider = siteIdProvider;
        this.pseudonymKeyProvider = pseudonymKeyProvider;
        this.expiresInProvider = expiresInProvider;
    }

    @Override
    public int onVerificationSessionReceived(
            String accountId, AuthMatchData authData, VerificationSession session, Dispatcher dispatcher) {
        Verification pendingVerification = new Verification(accountId, authData, session);
        putPendingVerification(pendingVerification, dispatcher);
        return 200;
    }

    @Override
    public int onSignedAgeCertificateReceived(SignedAgeCertificate signedCertificate) {
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

        VerifiedUser localUser = localizeUser(certificate.verifiedUser());
        return trySaveUser(pendingVerification.accountId(), localUser);
    }

    /** Puts a pending verification. */
    private void putPendingVerification(Verification pendingVerification, Dispatcher dispatcher) {
        PendingStore<Verification> pendingVerifications =
                pendingStoreFactory.getOrCreate(VERIFICATION_STORE_NAME, new TypeReference<>() {});
        VerificationRequest request = pendingVerification.verificationSession().verificationRequest();
        SecureId requestId = request.id();
        long expiration = request.expiration();
        pendingVerifications.put(requestId.toString(), pendingVerification, expiration, dispatcher.getIoThread());
    }

    /** Verifies a {@link SignedAgeCertificate}, returning a status code. */
    private int verifySignedAgeCertificate(SignedAgeCertificate signedCertificate) {
        if (!signedCertificate.verify(avsPublicSigningKeyProvider.get())) {
            return 401;
        }

        VerificationRequest request = signedCertificate.ageCertificate().verificationRequest();
        if (!request.isIntendedRecipient(siteIdProvider.get())) {
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
                pendingStoreFactory.getOrCreate(VERIFICATION_STORE_NAME, new TypeReference<>() {});
        return pendingVerifications.tryRemove(requestId.toString());
    }

    /** Runs an authentication check, returning a status code. */
    private int authenticate(AuthMatchData authData, AesGcmEncryptionPackage remoteAuthToken, Aes256Key key) {
        HttpOptional<AuthMatchData> maybeRemoteAuthData = authDataExtractor.tryDecrypt(remoteAuthToken, key);
        if (maybeRemoteAuthData.isEmpty()) {
            return maybeRemoteAuthData.statusCode();
        }
        AuthMatchData remoteAuthData = maybeRemoteAuthData.get();

        return authData.match(remoteAuthData) ? 200 : 403;
    }

    /** Localizes a {@link VerifiedUser} for the site. */
    private VerifiedUser localizeUser(VerifiedUser user) {
        return user.localize(pseudonymKeyProvider.get());
    }

    /** Saves a {@link VerifiedUser} for the account, returning a status code. */
    private int trySaveUser(String accountId, VerifiedUser user) {
        long now = System.currentTimeMillis() / 1000;
        long expiration = now + expiresInProvider.get().toSeconds();
        VerificationState state = VerificationState.verified(user, expiration);
        Optional<String> maybeDuplicateAccountId = verificationStore.trySave(accountId, state);
        return maybeDuplicateAccountId.isEmpty() ? 200 : 409;
    }

    /** Pending verification. */
    public record Verification(String accountId, AuthMatchData authData, VerificationSession verificationSession) {}
}
