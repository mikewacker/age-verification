package org.example.age.service.verification.internal;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.mikewacker.drift.api.HttpOptional;
import io.github.mikewacker.drift.api.ScheduledExecutor;
import java.time.Duration;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.example.age.api.def.AuthMatchData;
import org.example.age.api.def.VerificationState;
import org.example.age.api.def.VerificationStatus;
import org.example.age.data.certificate.AgeCertificate;
import org.example.age.data.certificate.SignedAgeCertificate;
import org.example.age.data.certificate.VerificationRequest;
import org.example.age.data.certificate.VerificationSession;
import org.example.age.data.crypto.AesGcmEncryptionPackage;
import org.example.age.data.crypto.SecureId;
import org.example.age.data.user.VerifiedUser;
import org.example.age.service.config.AvsConfig;
import org.example.age.service.config.RefreshableAvsConfigProvider;
import org.example.age.service.config.RefreshableRegisteredSiteConfigProvider;
import org.example.age.service.config.RegisteredSiteConfig;
import org.example.age.service.crypto.internal.AgeCertificateSigner;
import org.example.age.service.crypto.internal.AuthMatchDataEncryptor;
import org.example.age.service.crypto.internal.VerifiedUserLocalizer;
import org.example.age.service.store.PendingStore;
import org.example.age.service.store.PendingStoreFactory;
import org.example.age.service.store.VerificationStore;

@Singleton
final class AvsVerificationManagerImpl implements AvsVerificationManager {

    private final VerificationStore verificationStore;
    private final PendingStoreFactory pendingStoreFactory;
    private final AgeCertificateSigner certificateSigner;
    private final VerifiedUserLocalizer userLocalizer;
    private final AuthMatchDataEncryptor authDataEncryptor;
    private final RefreshableRegisteredSiteConfigProvider siteConfigProvider;
    private final RefreshableAvsConfigProvider avsConfigProvider;

    @Inject
    public AvsVerificationManagerImpl(
            VerificationStore verificationStore,
            PendingStoreFactory pendingStoreFactory,
            AgeCertificateSigner certificateSigner,
            VerifiedUserLocalizer userLocalizer,
            AuthMatchDataEncryptor authDataEncryptor,
            RefreshableRegisteredSiteConfigProvider siteConfigProvider,
            RefreshableAvsConfigProvider avsConfigProvider) {
        this.verificationStore = verificationStore;
        this.pendingStoreFactory = pendingStoreFactory;
        this.certificateSigner = certificateSigner;
        this.userLocalizer = userLocalizer;
        this.authDataEncryptor = authDataEncryptor;
        this.siteConfigProvider = siteConfigProvider;
        this.avsConfigProvider = avsConfigProvider;
    }

    @Override
    public VerificationState getVerificationState(String accountId) {
        return verificationStore.load(accountId);
    }

    @Override
    public HttpOptional<VerificationSession> createVerificationSession(String siteId, ScheduledExecutor executor) {
        HttpOptional<RegisteredSiteConfig> maybeSiteConfig = tryGetSiteConfig(siteId);
        if (maybeSiteConfig.isEmpty()) {
            return maybeSiteConfig.convertEmpty();
        }
        RegisteredSiteConfig siteConfig = maybeSiteConfig.get();

        VerificationSession session = createVerificationSession(siteId);
        Verification pendingVerification = new Verification(siteConfig, session);
        putUnlinkedPendingVerification(pendingVerification, executor);
        return HttpOptional.of(session);
    }

    @Override
    public int linkVerificationRequest(String accountId, SecureId requestId, ScheduledExecutor executor) {
        HttpOptional<VerifiedUser> maybeUser = tryGetVerifiedUser(accountId);
        if (maybeUser.isEmpty()) {
            return maybeUser.statusCode();
        }

        return tryLinkPendingVerification(accountId, requestId, executor);
    }

    @Override
    public HttpOptional<SignedAgeCertificate> createAgeCertificate(String accountId, AuthMatchData authData) {
        HttpOptional<VerifiedUser> maybeUser = tryGetVerifiedUser(accountId);
        if (maybeUser.isEmpty()) {
            return maybeUser.convertEmpty();
        }
        VerifiedUser user = maybeUser.get();

        HttpOptional<Verification> maybePendingVerification = tryRemoveLinkedPendingVerification(accountId);
        if (maybePendingVerification.isEmpty()) {
            return maybePendingVerification.convertEmpty();
        }
        Verification pendingVerification = maybePendingVerification.get();

        VerifiedUser localUser = localizeUserForSite(user, pendingVerification.siteConfig());
        SignedAgeCertificate signedCertificate =
                createAgeCertificate(localUser, authData, pendingVerification.verificationSession());
        return HttpOptional.of(signedCertificate);
    }

    /** Gets the {@link RegisteredSiteConfig} for a site, or returns a 404 error. */
    private HttpOptional<RegisteredSiteConfig> tryGetSiteConfig(String siteId) {
        Optional<RegisteredSiteConfig> maybeSiteConfig = siteConfigProvider.tryGet(siteId);
        return HttpOptional.fromOptional(maybeSiteConfig, 404);
    }

    /** Gets the {@link VerifiedUser} for a person, or returns a 403 error if the person is not verified. */
    private HttpOptional<VerifiedUser> tryGetVerifiedUser(String accountId) {
        VerificationState state = getVerificationState(accountId);
        return state.status().equals(VerificationStatus.VERIFIED)
                ? HttpOptional.of(state.verifiedUser())
                : HttpOptional.empty(403);
    }

    /** Localizes a {@link VerifiedUser} for a site. */
    private VerifiedUser localizeUserForSite(VerifiedUser user, RegisteredSiteConfig siteConfig) {
        VerifiedUser anonymizedUser = user.anonymizeAge(siteConfig.ageThresholds());
        return userLocalizer.localize(anonymizedUser, siteConfig.id());
    }

    /** Creates a {@link VerificationSession} for a site. */
    private VerificationSession createVerificationSession(String siteId) {
        AvsConfig avsConfig = avsConfigProvider.get();
        Duration expiresIn = Duration.ofSeconds(avsConfig.verificationSessionExpiresIn());
        String redirectPath = avsConfig.redirectPath();
        VerificationRequest request = VerificationRequest.generateForSite(siteId, expiresIn, redirectPath);
        return VerificationSession.generate(request);
    }

    /** Creates a {@link SignedAgeCertificate} for a {@link VerifiedUser}. */
    private SignedAgeCertificate createAgeCertificate(
            VerifiedUser user, AuthMatchData authData, VerificationSession session) {
        AesGcmEncryptionPackage authToken = authDataEncryptor.encrypt(authData, session.authKey());
        AgeCertificate certificate = AgeCertificate.of(session.verificationRequest(), user, authToken);
        return certificateSigner.sign(certificate);
    }

    /** Puts an unlinked pending verification. */
    private void putUnlinkedPendingVerification(Verification pendingVerification, ScheduledExecutor executor) {
        PendingStore<Verification> unlinkedPendingVerifications = getUnlinkedPendingVerifications();
        VerificationRequest request = pendingVerification.verificationSession().verificationRequest();
        unlinkedPendingVerifications.put(request.id().toString(), pendingVerification, request.expiration(), executor);
    }

    /** Links the pending verification for the request ID to an account ID, or returns a 404 error. */
    private int tryLinkPendingVerification(String accountId, SecureId requestId, ScheduledExecutor executor) {
        PendingStore<Verification> unlinkedPendingVerifications = getUnlinkedPendingVerifications();
        Optional<Verification> maybePendingVerification = unlinkedPendingVerifications.tryRemove(requestId.toString());
        if (maybePendingVerification.isEmpty()) {
            return 404;
        }
        Verification pendingVerification = maybePendingVerification.get();

        PendingStore<Verification> linkedPendingVerifications = getLinkedPendingVerifications();
        VerificationRequest request = pendingVerification.verificationSession().verificationRequest();
        linkedPendingVerifications.put(accountId, pendingVerification, request.expiration(), executor);
        return 200;
    }

    /** Removes and returns the pending verification for the account ID, or returns a 404 error. */
    private HttpOptional<Verification> tryRemoveLinkedPendingVerification(String accountId) {
        PendingStore<Verification> linkedPendingVerifications = getLinkedPendingVerifications();
        Optional<Verification> maybePendingVerification = linkedPendingVerifications.tryRemove(accountId);
        return HttpOptional.fromOptional(maybePendingVerification, 404);
    }

    /** Gets the store for unlinked pending verifications. */
    private PendingStore<Verification> getUnlinkedPendingVerifications() {
        return pendingStoreFactory.getOrCreate("unlinkedVerification", new TypeReference<>() {});
    }

    /** Gets the store for linked pending verifications. */
    private PendingStore<Verification> getLinkedPendingVerifications() {
        return pendingStoreFactory.getOrCreate("linkedVerification", new TypeReference<>() {});
    }

    /** Pending verification. */
    public record Verification(RegisteredSiteConfig siteConfig, VerificationSession verificationSession) {}
}
