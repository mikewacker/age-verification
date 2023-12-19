package org.example.age.service.verification.internal.site;

import java.time.Duration;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.example.age.api.base.HttpOptional;
import org.example.age.api.def.common.VerificationState;
import org.example.age.data.certificate.SignedAgeCertificate;
import org.example.age.data.user.VerifiedUser;
import org.example.age.service.crypto.internal.common.AgeCertificateVerifier;
import org.example.age.service.store.common.VerificationStore;

@Singleton
final class FakeSiteVerificationProcessorImpl implements FakeSiteVerificationProcessor {

    private final VerificationStore verificationStore;
    private final AgeCertificateVerifier certificateVerifier;

    private String storedAccountId = null;

    @Inject
    public FakeSiteVerificationProcessorImpl(
            VerificationStore verificationStore, AgeCertificateVerifier certificateVerifier) {
        this.verificationStore = verificationStore;
        this.certificateVerifier = certificateVerifier;
    }

    @Override
    public VerificationState getVerificationState(String accountId) {
        return verificationStore.load(accountId);
    }

    @Override
    public void beginVerification(String accountId) {
        reset();
        storedAccountId = accountId;
    }

    @Override
    public HttpOptional<String> onAgeCertificateReceived(SignedAgeCertificate signedCertificate) {
        if (!certificateVerifier.verify(signedCertificate)) {
            return HttpOptional.empty(401);
        }

        if (storedAccountId == null) {
            reset();
            return HttpOptional.empty(418);
        }

        String accountId = storedAccountId;
        reset();

        VerifiedUser user = signedCertificate.ageCertificate().verifiedUser();
        long expiration = createExpiration();
        VerificationState state = VerificationState.verified(user, expiration);
        Optional<String> maybeConflictingAccountId = verificationStore.trySave(accountId, state);
        if (maybeConflictingAccountId.isPresent()) {
            return HttpOptional.empty(409);
        }

        return HttpOptional.of("/api/verification-state");
    }

    /** Creates an expiration timestamp in seconds. */
    private static long createExpiration() {
        long now = System.currentTimeMillis() / 1000;
        return now + Duration.ofDays(30).toSeconds();
    }

    /** Clears the stored verification data. */
    private void reset() {
        storedAccountId = null;
    }
}
