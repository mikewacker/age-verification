package org.example.age.common.site.verification.internal;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import java.time.Duration;
import java.util.Optional;
import java.util.function.Supplier;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import org.example.age.common.site.verification.VerificationState;
import org.example.age.common.site.verification.VerificationStore;
import org.example.age.common.store.internal.PendingStore;
import org.example.age.data.SecureId;
import org.example.age.data.VerifiedUser;
import org.example.age.data.certificate.AgeCertificate;
import org.example.age.data.certificate.VerificationSession;
import org.xnio.XnioExecutor;

@Singleton
final class VerificationManagerImpl implements VerificationManager {

    private final VerificationStore verificationStore;
    private final Supplier<SecureId> pseudonymKeySupplier;
    private final Supplier<Duration> expiresInSupplier;

    private final PendingStore<SecureId, String> pendingVerifications = PendingStore.create();

    @Inject
    public VerificationManagerImpl(
            VerificationStore verificationStore,
            @Named("pseudonymKey") Supplier<SecureId> pseudonymKeySupplier,
            @Named("expiresIn") Supplier<Duration> expiresInSupplier) {
        this.verificationStore = verificationStore;
        this.pseudonymKeySupplier = pseudonymKeySupplier;
        this.expiresInSupplier = expiresInSupplier;
    }

    @Override
    public int onVerificationSessionReceived(
            String accountId, VerificationSession session, HttpServerExchange exchange) {
        SecureId requestId = session.verificationRequest().id();
        long expiration = session.verificationRequest().expiration();
        XnioExecutor executor = exchange.getIoThread();
        boolean wasPut = pendingVerifications.put(requestId, accountId, expiration, executor);
        return wasPut ? StatusCodes.OK : StatusCodes.BAD_GATEWAY;
    }

    @Override
    public int onAgeCertificateReceived(AgeCertificate certificate) {
        SecureId requestId = certificate.verificationRequest().id();
        Optional<String> maybeAccountId = pendingVerifications.tryRemove(requestId);
        if (maybeAccountId.isEmpty()) {
            return StatusCodes.NOT_FOUND;
        }

        String accountId = maybeAccountId.get();
        VerifiedUser user = certificate.verifiedUser();
        VerifiedUser localizedUser = user.localize(pseudonymKeySupplier.get());
        VerificationState state = createVerifiedState(localizedUser);
        Optional<String> maybeDuplicateAccountId = verificationStore.trySave(accountId, state);
        return maybeDuplicateAccountId.isEmpty() ? StatusCodes.OK : StatusCodes.CONFLICT;
    }

    /** Creates a verified {@link VerificationState}. */
    private VerificationState createVerifiedState(VerifiedUser user) {
        long now = System.currentTimeMillis() / 1000;
        long expiration = now + expiresInSupplier.get().toSeconds();
        return VerificationState.verified(user, expiration);
    }
}
