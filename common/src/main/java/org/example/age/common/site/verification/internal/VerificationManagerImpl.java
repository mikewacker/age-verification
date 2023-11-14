package org.example.age.common.site.verification.internal;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import java.time.Duration;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import org.example.age.common.base.store.PendingStore;
import org.example.age.common.base.store.PendingStoreFactory;
import org.example.age.common.base.utils.internal.PendingStoreUtils;
import org.example.age.common.site.store.VerificationState;
import org.example.age.common.site.store.VerificationStore;
import org.example.age.data.VerifiedUser;
import org.example.age.data.certificate.AgeCertificate;
import org.example.age.data.certificate.VerificationSession;
import org.example.age.data.crypto.SecureId;

@Singleton
final class VerificationManagerImpl implements VerificationManager {

    private final VerificationStore verificationStore;
    private final PendingStore<SecureId, String> pendingVerifications;
    private final Provider<SecureId> pseudonymKeyProvider;
    private final Provider<Duration> expiresInProvider;

    @Inject
    public VerificationManagerImpl(
            VerificationStore verificationStore,
            PendingStoreFactory pendingStoreFactory,
            @Named("pseudonymKey") Provider<SecureId> pseudonymKeyProvider,
            @Named("expiresIn") Provider<Duration> expiresInProvider) {
        this.verificationStore = verificationStore;
        this.pendingVerifications = pendingStoreFactory.create();
        this.pseudonymKeyProvider = pseudonymKeyProvider;
        this.expiresInProvider = expiresInProvider;
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
        VerifiedUser localizedUser = user.localize(pseudonymKeyProvider.get());
        long now = System.currentTimeMillis() / 1000;
        long expiration = now + expiresInProvider.get().toSeconds();
        return VerificationState.verified(localizedUser, expiration);
    }
}
