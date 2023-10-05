package org.example.age.common.verification;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import java.time.Duration;
import java.util.Optional;
import java.util.function.Supplier;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import org.example.age.certificate.AgeCertificate;
import org.example.age.certificate.AuthKey;
import org.example.age.certificate.VerificationSession;
import org.example.age.common.verification.auth.AuthMatchData;
import org.example.age.common.verification.auth.AuthMatchDataExtractor;
import org.example.age.common.verification.internal.PendingStore;
import org.example.age.data.SecureId;
import org.example.age.data.VerifiedUser;

@Singleton
final class VerificationStateManagerImpl implements VerificationStateManager {

    private final VerifiedUserStore userStore;
    private final AuthMatchDataExtractor authDataExtractor;
    private final Supplier<Duration> expiresInSupplier;

    private final PendingStore<SecureId, PendingVerification> pendingVerifications = PendingStore.create();

    @Inject
    public VerificationStateManagerImpl(
            VerifiedUserStore userStore,
            AuthMatchDataExtractor authDataExtractor,
            @Named("expiresIn") Supplier<Duration> expiresInSupplier) {
        this.userStore = userStore;
        this.authDataExtractor = authDataExtractor;
        this.expiresInSupplier = expiresInSupplier;
    }

    @Override
    public VerificationState getVerificationState(HttpServerExchange exchange) {
        Optional<String> maybeAccountId = userStore.tryGetAccountId(exchange);
        if (maybeAccountId.isEmpty()) {
            return VerificationState.unverified();
        }

        String accountId = maybeAccountId.get();
        return userStore.load(accountId);
    }

    @Override
    public int onVerificationSessionReceived(VerificationSession session, HttpServerExchange exchange) {
        Optional<String> maybeAccountId = userStore.tryGetAccountId(exchange);
        if (maybeAccountId.isEmpty()) {
            return StatusCodes.NOT_FOUND;
        }

        String accountId = maybeAccountId.get();
        PendingVerification pendingVerification = createPendingVerification(accountId, session, exchange);
        boolean wasPut = pendingVerifications.put(
                session.verificationRequest().id(),
                pendingVerification,
                session.verificationRequest().expiration(),
                exchange.getIoThread());
        return wasPut ? StatusCodes.OK : StatusCodes.BAD_GATEWAY;
    }

    @Override
    public int onAgeCertificateReceived(AgeCertificate certificate) {
        Optional<PendingVerification> maybePendingVerification =
                pendingVerifications.tryRemove(certificate.verificationRequest().id());
        if (maybePendingVerification.isEmpty()) {
            return StatusCodes.NOT_FOUND;
        }

        PendingVerification pendingVerification = maybePendingVerification.get();
        if (!authenticate(certificate, pendingVerification)) {
            return StatusCodes.FORBIDDEN;
        }

        String accountId = pendingVerification.accountId();
        VerifiedUser user = certificate.verifiedUser();
        if (!checkNoDuplicateVerifications(accountId, user)) {
            return StatusCodes.CONFLICT;
        }

        verify(accountId, user);
        return StatusCodes.OK;
    }

    /** Creates a {@link PendingVerification} for an account as part of a {@link VerificationSession}. */
    private PendingVerification createPendingVerification(
            String accountId, VerificationSession session, HttpServerExchange exchange) {
        AuthMatchData localAuthData = authDataExtractor.extract(exchange);
        return new PendingVerification(session.verificationRequest().id(), accountId, localAuthData, session.authKey());
    }

    /** Checks that the person who requested the age certificate is the same as the account owner. */
    private boolean authenticate(AgeCertificate certificate, PendingVerification pendingVerification) {
        AuthMatchData remoteAuthData;
        try {
            remoteAuthData = authDataExtractor.decrypt(certificate.authToken(), pendingVerification.authKey());
        } catch (RuntimeException e) {
            return false;
        }

        return pendingVerification.localAuthData().match(remoteAuthData);
    }

    /** Checks that the one {@link VerifiedUser} is only used to verify one account. */
    private boolean checkNoDuplicateVerifications(String accountId, VerifiedUser user) {
        Optional<String> maybeVerifiedAccountId = userStore.tryGetAccountId(user);
        if (maybeVerifiedAccountId.isEmpty()) {
            return true;
        }

        String verifiedAccountId = maybeVerifiedAccountId.get();
        return verifiedAccountId.equals(accountId);
    }

    /** Verifies an account. */
    private void verify(String accountId, VerifiedUser user) {
        long now = System.currentTimeMillis() / 1000;
        long expiration = now + expiresInSupplier.get().toSeconds();
        VerificationState state = VerificationState.verified(user, expiration);
        userStore.save(accountId, state);
    }

    /** Pending verification for an account. */
    @SuppressWarnings("UnusedVariable") // false positive, see https://github.com/google/error-prone/issues/2713
    private record PendingVerification(
            SecureId requestId, String accountId, AuthMatchData localAuthData, AuthKey authKey) {}
}
