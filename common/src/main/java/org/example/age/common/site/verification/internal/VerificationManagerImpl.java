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
import org.example.age.common.store.PendingStore;
import org.example.age.common.store.PendingStoreFactory;
import org.example.age.common.utils.internal.PendingStoreUtils;
import org.example.age.data.SecureId;
import org.example.age.data.VerifiedUser;
import org.example.age.data.certificate.AgeCertificate;
import org.example.age.data.certificate.VerificationSession;

@Singleton
final class VerificationManagerImpl implements VerificationManager {

    private final VerificationStore verificationStore;
    private final PendingStore<SecureId, String> pendingVerifications;
    private final Supplier<SecureId> pseudonymKeySupplier;
    private final Supplier<Duration> expiresInSupplier;

    @Inject
    public VerificationManagerImpl(
            VerificationStore verificationStore,
            PendingStoreFactory pendingStoreFactory,
            @Named("pseudonymKey") Supplier<SecureId> pseudonymKeySupplier,
            @Named("expiresIn") Supplier<Duration> expiresInSupplier) {
        this.verificationStore = verificationStore;
        this.pendingVerifications = pendingStoreFactory.create();
        this.pseudonymKeySupplier = pseudonymKeySupplier;
        this.expiresInSupplier = expiresInSupplier;
    }

    @Override
    public void onVerificationSessionReceived(
            String accountId, VerificationSession session, HttpServerExchange exchange) {
        SecureId requestId = session.verificationRequest().id();
        PendingStoreUtils.putForVerificationSession(pendingVerifications, requestId, accountId, session, exchange);
    }

    @Override
    public int onAgeCertificateReceived(AgeCertificate certificate) {
        SecureId requestId = certificate.verificationRequest().id();
        Optional<String> maybeAccountId = pendingVerifications.tryRemove(requestId);
        if (maybeAccountId.isEmpty()) {
            return StatusCodes.NOT_FOUND;
        }

        String accountId = maybeAccountId.get();
        VerificationState state = createVerifiedState(certificate);
        Optional<String> maybeDuplicateAccountId = verificationStore.trySave(accountId, state);
        return maybeDuplicateAccountId.isEmpty() ? StatusCodes.OK : StatusCodes.CONFLICT;
    }

    /** Creates a verified {@link VerificationState} from an {@link AgeCertificate}. */
    private VerificationState createVerifiedState(AgeCertificate certificate) {
        VerifiedUser user = certificate.verifiedUser();
        VerifiedUser localizedUser = user.localize(pseudonymKeySupplier.get());
        long now = System.currentTimeMillis() / 1000;
        long expiration = now + expiresInSupplier.get().toSeconds();
        return VerificationState.verified(localizedUser, expiration);
    }
}
