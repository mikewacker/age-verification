package org.example.age.common.avs.verification.internal;

import com.google.common.net.HostAndPort;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import java.time.Duration;
import java.util.Optional;
import java.util.function.Supplier;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import org.example.age.common.avs.store.SiteConfig;
import org.example.age.common.avs.store.SiteConfigStore;
import org.example.age.common.avs.store.VerifiedUserStore;
import org.example.age.common.base.auth.AuthMatchData;
import org.example.age.common.base.auth.AuthMatchDataExtractor;
import org.example.age.common.base.store.PendingStore;
import org.example.age.common.base.store.PendingStoreFactory;
import org.example.age.common.base.utils.internal.HttpOptional;
import org.example.age.common.base.utils.internal.PendingStoreUtils;
import org.example.age.data.SecureId;
import org.example.age.data.VerifiedUser;
import org.example.age.data.certificate.AgeCertificate;
import org.example.age.data.certificate.AuthToken;
import org.example.age.data.certificate.VerificationRequest;
import org.example.age.data.certificate.VerificationSession;

@Singleton
final class VerificationManagerImpl implements VerificationManager {

    private final VerifiedUserStore userStore;
    private final SiteConfigStore siteConfigStore;
    private final AuthMatchDataExtractor authDataExtractor;
    private final PendingStore<SecureId, PendingVerification> unlinkedPendingVerifications;
    private final PendingStore<String, PendingVerification> linkedPendingVerifications;
    private final Supplier<Duration> expiresInSupplier;

    @Inject
    public VerificationManagerImpl(
            VerifiedUserStore userStore,
            SiteConfigStore siteConfigStore,
            AuthMatchDataExtractor authDataExtractor,
            PendingStoreFactory pendingStoreFactory,
            @Named("expiresIn") Supplier<Duration> expiresInSupplier) {
        this.userStore = userStore;
        this.siteConfigStore = siteConfigStore;
        this.authDataExtractor = authDataExtractor;
        unlinkedPendingVerifications = pendingStoreFactory.create();
        linkedPendingVerifications = pendingStoreFactory.create();
        this.expiresInSupplier = expiresInSupplier;
    }

    @Override
    public HttpOptional<VerificationSession> createVerificationSession(String siteId, HttpServerExchange exchange) {
        Optional<SiteConfig> maybeSiteConfig = siteConfigStore.tryLoad(siteId);
        if (maybeSiteConfig.isEmpty()) {
            return HttpOptional.empty(StatusCodes.NOT_FOUND);
        }

        SiteConfig siteConfig = maybeSiteConfig.get();
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
        AgeCertificate certificate = createAgeCertificate(user, pendingVerification, exchange);
        HostAndPort location = pendingVerification.siteConfig().siteLocation();
        Verification verification = Verification.of(certificate, location);
        return HttpOptional.of(verification);
    }

    /** Creates a {@link VerificationSession} for the site. */
    private VerificationSession createVerificationSession(String siteId) {
        VerificationRequest request = VerificationRequest.generateForSite(siteId, expiresInSupplier.get());
        return VerificationSession.create(request);
    }

    /** Creates an {@link AgeCertificate} from a {@link VerifiedUser} and a pending verification request. */
    private AgeCertificate createAgeCertificate(
            VerifiedUser user, PendingVerification pendingVerification, HttpServerExchange exchange) {
        VerificationRequest request = pendingVerification.verificationSession().verificationRequest();
        VerifiedUser localizedUser = localizeUser(user, pendingVerification.siteConfig());
        AuthToken authToken = extractAuthToken(exchange, pendingVerification.verificationSession());
        return AgeCertificate.of(request, localizedUser, authToken);
    }

    /** Localizes a {@link VerifiedUser} for a site using the {@link SiteConfig}. */
    private static VerifiedUser localizeUser(VerifiedUser user, SiteConfig siteConfig) {
        return user.localize(siteConfig.pseudonymKey()).anonymizeAge(siteConfig.ageThresholds());
    }

    /** Extracts an encrypted {@link AuthToken} from an {@link HttpServerExchange}. */
    private AuthToken extractAuthToken(HttpServerExchange exchange, VerificationSession session) {
        AuthMatchData authData = authDataExtractor.extract(exchange);
        return authData.encrypt(session.authKey());
    }

    /** Pending verification request for a specific site. */
    private record PendingVerification(VerificationSession verificationSession, SiteConfig siteConfig) {}
}
