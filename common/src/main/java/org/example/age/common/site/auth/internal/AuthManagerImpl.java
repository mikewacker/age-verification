package org.example.age.common.site.auth.internal;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.example.age.common.auth.AuthMatchData;
import org.example.age.common.auth.AuthMatchDataExtractor;
import org.example.age.common.store.internal.PendingStore;
import org.example.age.data.SecureId;
import org.example.age.data.certificate.AgeCertificate;
import org.example.age.data.certificate.AuthKey;
import org.example.age.data.certificate.AuthToken;
import org.example.age.data.certificate.VerificationSession;
import org.xnio.XnioExecutor;

@Singleton
final class AuthManagerImpl implements AuthManager {

    private final AuthMatchDataExtractor authDataExtractor;

    private final PendingStore<SecureId, PendingAuth> pendingAuths = PendingStore.create();

    @Inject
    public AuthManagerImpl(AuthMatchDataExtractor authDataExtractor) {
        this.authDataExtractor = authDataExtractor;
    }

    @Override
    public void onVerificationSessionReceived(VerificationSession session, HttpServerExchange exchange) {
        SecureId requestId = session.verificationRequest().id();
        PendingAuth pendingAuth = createPendingAuth(session, exchange);
        long expiration = session.verificationRequest().expiration();
        XnioExecutor executor = exchange.getIoThread();
        pendingAuths.put(requestId, pendingAuth, expiration, executor);
    }

    @Override
    public int onAgeCertificateReceived(AgeCertificate certificate) {
        SecureId requestId = certificate.verificationRequest().id();
        Optional<PendingAuth> maybePendingAuth = pendingAuths.tryRemove(requestId);
        if (maybePendingAuth.isEmpty()) {
            return StatusCodes.NOT_FOUND;
        }

        PendingAuth pendingAuth = maybePendingAuth.get();
        AuthToken remoteAuthToken = certificate.authToken();
        AuthMatchData remoteAuthData;
        try {
            remoteAuthData = authDataExtractor.decrypt(remoteAuthToken, pendingAuth.key());
        } catch (RuntimeException e) {
            return StatusCodes.UNAUTHORIZED;
        }

        boolean matches = pendingAuth.localData().match(remoteAuthData);
        return matches ? StatusCodes.OK : StatusCodes.UNAUTHORIZED;
    }

    /** Creates a {@link PendingAuth}. */
    private PendingAuth createPendingAuth(VerificationSession session, HttpServerExchange exchange) {
        AuthMatchData localAuthData = authDataExtractor.extract(exchange);
        AuthKey authKey = session.authKey();
        return new PendingAuth(localAuthData, authKey);
    }

    @SuppressWarnings("UnusedVariable") // false positive, see https://github.com/google/error-prone/issues/2713
    private record PendingAuth(AuthMatchData localData, AuthKey key) {}
}
