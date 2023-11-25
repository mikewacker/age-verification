package org.example.age.common.avs.verification.internal;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import java.time.Duration;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import org.example.age.api.HttpOptional;
import org.example.age.common.api.data.AuthMatchData;
import org.example.age.common.api.data.AuthMatchDataExtractor;
import org.example.age.common.avs.config.RegisteredSiteConfig;
import org.example.age.common.avs.store.RegisteredSiteConfigStore;
import org.example.age.common.avs.store.VerifiedUserStore;
import org.example.age.common.base.store.PendingStore;
import org.example.age.common.base.store.PendingStoreFactory;
import org.example.age.common.base.utils.internal.PendingStoreUtils;
import org.example.age.common.service.config.SiteLocation;
import org.example.age.common.service.data.internal.AuthMatchDataEncryptor;
import org.example.age.data.certificate.AgeCertificate;
import org.example.age.data.certificate.VerificationRequest;
import org.example.age.data.certificate.VerificationSession;
import org.example.age.data.crypto.AesGcmEncryptionPackage;
import org.example.age.data.crypto.SecureId;
import org.example.age.data.user.VerifiedUser;

@Singleton
final class VerificationManagerImpl implements VerificationManager {

    private final AuthMatchDataExtractor authDataExtractor;
    private final RegisteredSiteConfigStore siteConfigStore;
    private final VerifiedUserStore userStore;
    private final AuthMatchDataEncryptor authDataEncryptor;
    private final PendingStore<SecureId, PendingVerification> unlinkedPendingVerifications;
    private final PendingStore<String, PendingVerification> linkedPendingVerifications;
    private final Provider<Duration> expiresInProvider;

    @Inject
    public VerificationManagerImpl(
            AuthMatchDataExtractor authDataExtractor,
            RegisteredSiteConfigStore siteConfigStore,
            VerifiedUserStore userStore,
            AuthMatchDataEncryptor authDataEncryptor,
            PendingStoreFactory pendingStoreFactory,
            @Named("expiresIn") Provider<Duration> expiresInProvider) {
        this.authDataExtractor = authDataExtractor;
        this.siteConfigStore = siteConfigStore;
        this.userStore = userStore;
        this.authDataEncryptor = authDataEncryptor;
        unlinkedPendingVerifications = pendingStoreFactory.create();
        linkedPendingVerifications = pendingStoreFactory.create();
        this.expiresInProvider = expiresInProvider;
    }

    @Override
    public HttpOptional<VerificationSession> createVerificationSession(String siteId, HttpServerExchange exchange) {
        Optional<RegisteredSiteConfig> maybeSiteConfig = siteConfigStore.tryLoad(siteId);
        if (maybeSiteConfig.isEmpty()) {
            return HttpOptional.empty(StatusCodes.NOT_FOUND);
        }

        RegisteredSiteConfig siteConfig = maybeSiteConfig.get();
        VerificationSession session = createVerificationSession(siteId);
        PendingVerification pendingVerification = new PendingVerification(session, siteConfig);
        SecureId requestId = session.verificationRequest().id();
        PendingStoreUtils.putForVerificationSession(
                unlinkedPendingVerifications, requestId, pendingVerification, session, exchange);
        return HttpOptional.of(session);
    }

    @Override
    public int linkVerificationRequest(String accountId, SecureId requestId, HttpServerExchange exchange) {
        Optional<VerifiedUser> maybeUser = userStore.tryLoad(accountId);
        if (maybeUser.isEmpty()) {
            return StatusCodes.FORBIDDEN;
        }

        Optional<PendingVerification> maybePendingVerification = unlinkedPendingVerifications.tryRemove(requestId);
        if (maybePendingVerification.isEmpty()) {
            return StatusCodes.NOT_FOUND;
        }

        PendingVerification pendingVerification = maybePendingVerification.get();
        VerificationSession session = pendingVerification.verificationSession();
        PendingStoreUtils.putForVerificationSession(
                linkedPendingVerifications, accountId, pendingVerification, session, exchange);
        return StatusCodes.OK;
    }

    @Override
    public HttpOptional<Verification> createAgeCertificate(String accountId, HttpServerExchange exchange) {
        Optional<VerifiedUser> maybeUser = userStore.tryLoad(accountId);
        if (maybeUser.isEmpty()) {
            return HttpOptional.empty(StatusCodes.FORBIDDEN);
        }

        VerifiedUser user = maybeUser.get();
        Optional<PendingVerification> maybePendingVerification = linkedPendingVerifications.tryRemove(accountId);
        if (maybePendingVerification.isEmpty()) {
            return HttpOptional.empty(StatusCodes.NOT_FOUND);
        }

        PendingVerification pendingVerification = maybePendingVerification.get();
        HttpOptional<AesGcmEncryptionPackage> maybeAuthToken =
                tryExtractAuthToken(exchange, pendingVerification.verificationSession());
        if (maybeAuthToken.isEmpty()) {
            return HttpOptional.empty(maybeAuthToken.statusCode());
        }

        AesGcmEncryptionPackage authToken = maybeAuthToken.get();
        AgeCertificate certificate = createAgeCertificate(user, pendingVerification, authToken);
        SiteLocation location = pendingVerification.siteConfig().siteLocation();
        Verification verification = Verification.of(certificate, location);
        return HttpOptional.of(verification);
    }

    /** Creates a {@link VerificationSession} for the site. */
    private VerificationSession createVerificationSession(String siteId) {
        VerificationRequest request = VerificationRequest.generateForSite(siteId, expiresInProvider.get());
        return VerificationSession.create(request);
    }

    /** Creates an {@link AgeCertificate} from a {@link VerifiedUser} and a pending verification request. */
    private AgeCertificate createAgeCertificate(
            VerifiedUser user, PendingVerification pendingVerification, AesGcmEncryptionPackage authToken) {
        VerificationRequest request = pendingVerification.verificationSession().verificationRequest();
        VerifiedUser localizedUser = localizeUser(user, pendingVerification.siteConfig());
        return AgeCertificate.of(request, localizedUser, authToken);
    }

    /** Localizes a {@link VerifiedUser} for a site using the {@link RegisteredSiteConfig}. */
    private static VerifiedUser localizeUser(VerifiedUser user, RegisteredSiteConfig siteConfig) {
        return user.localize(siteConfig.pseudonymKey()).anonymizeAge(siteConfig.ageThresholds());
    }

    /** Extracts an auth token from an {@link HttpServerExchange}. */
    private HttpOptional<AesGcmEncryptionPackage> tryExtractAuthToken(
            HttpServerExchange exchange, VerificationSession session) {
        HttpOptional<AuthMatchData> maybeAuthData = authDataExtractor.tryExtract(exchange);
        if (maybeAuthData.isEmpty()) {
            return HttpOptional.empty(maybeAuthData.statusCode());
        }

        AuthMatchData authData = maybeAuthData.get();
        AesGcmEncryptionPackage authToken = authDataEncryptor.encrypt(authData, session.authKey());
        return HttpOptional.of(authToken);
    }

    /** Pending verification request for a specific site. */
    private record PendingVerification(VerificationSession verificationSession, RegisteredSiteConfig siteConfig) {}
}
