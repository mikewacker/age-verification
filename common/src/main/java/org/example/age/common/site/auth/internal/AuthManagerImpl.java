package org.example.age.common.site.auth.internal;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.example.age.common.api.data.AuthMatchData;
import org.example.age.common.api.data.AuthMatchDataExtractor;
import org.example.age.common.base.store.PendingStore;
import org.example.age.common.base.store.PendingStoreFactory;
import org.example.age.common.base.utils.internal.PendingStoreUtils;
import org.example.age.data.certificate.AgeCertificate;
import org.example.age.data.certificate.VerificationSession;
import org.example.age.data.crypto.AuthKey;
import org.example.age.data.crypto.SecureId;

@Singleton
final class AuthManagerImpl implements AuthManager {

    private final AuthMatchDataExtractor authDataExtractor;
    private final PendingStore<SecureId, PendingAuth> pendingAuths;

    @Inject
    public AuthManagerImpl(AuthMatchDataExtractor authDataExtractor, PendingStoreFactory pendingStoreFactory) {
        this.authDataExtractor = authDataExtractor;
        this.pendingAuths = pendingStoreFactory.create();
    }

    @Override
    public void onVerificationSessionReceived(VerificationSession session, HttpServerExchange exchange) {
        SecureId requestId = session.verificationRequest().id();
        Optional<AuthMatchData> maybeAuthData = authDataExtractor.tryExtract(exchange, code -> {});
        if (maybeAuthData.isEmpty()) {
            return;
        }

        AuthMatchData authData = maybeAuthData.get();
        PendingAuth pendingAuth = new PendingAuth(authData, session.authKey());
        PendingStoreUtils.putForVerificationSession(pendingAuths, requestId, pendingAuth, session, exchange);
    }

    @Override
    public int onAgeCertificateReceived(AgeCertificate certificate) {
        SecureId requestId = certificate.verificationRequest().id();
        Optional<PendingAuth> maybePendingAuth = pendingAuths.tryRemove(requestId);
        if (maybePendingAuth.isEmpty()) {
            return StatusCodes.NOT_FOUND;
        }

        PendingAuth pendingAuth = maybePendingAuth.get();
        Optional<AuthMatchData> maybeRemoteAuthData =
                authDataExtractor.tryDecrypt(certificate.authToken(), pendingAuth.key(), code -> {});
        if (maybeRemoteAuthData.isEmpty()) {
            return StatusCodes.UNAUTHORIZED;
        }

        AuthMatchData remoteAuthData = maybeRemoteAuthData.get();
        boolean matches = pendingAuth.localData().match(remoteAuthData);
        return matches ? StatusCodes.OK : StatusCodes.UNAUTHORIZED;
    }

    @SuppressWarnings("UnusedVariable") // false positive, see https://github.com/google/error-prone/issues/2713
    private record PendingAuth(AuthMatchData localData, AuthKey key) {}
}
