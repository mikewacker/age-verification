package org.example.age.service.verification.internal;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.time.Duration;
import org.example.age.api.def.AuthMatchData;
import org.example.age.api.def.VerificationState;
import org.example.age.data.certificate.AgeCertificate;
import org.example.age.data.certificate.SignedAgeCertificate;
import org.example.age.data.certificate.VerificationRequest;
import org.example.age.data.certificate.VerificationSession;
import org.example.age.data.crypto.AesGcmEncryptionPackage;
import org.example.age.data.user.VerifiedUser;
import org.example.age.service.crypto.internal.AgeCertificateSigner;
import org.example.age.service.crypto.internal.AuthMatchDataEncryptor;
import org.example.age.service.store.VerificationStore;

@Singleton
final class FakeAvsVerificationFactoryImpl implements FakeAvsVerificationFactory {

    private final VerificationStore verificationStore;
    private final AgeCertificateSigner certificateSigner;
    private final AuthMatchDataEncryptor authDataEncryptor;

    @Inject
    public FakeAvsVerificationFactoryImpl(
            VerificationStore verificationStore,
            AgeCertificateSigner certificateSigner,
            AuthMatchDataEncryptor authDataEncryptor) {
        this.verificationStore = verificationStore;
        this.certificateSigner = certificateSigner;
        this.authDataEncryptor = authDataEncryptor;
    }

    @Override
    public VerificationState getVerificationState(String accountId) {
        return verificationStore.load(accountId);
    }

    @Override
    public VerificationSession createVerificationSession(String siteId) {
        return createVerificationSession(siteId, Duration.ofMinutes(5));
    }

    @Override
    public VerificationSession createVerificationSession(String siteId, Duration expiresIn) {
        VerificationRequest request =
                VerificationRequest.generateForSite(siteId, expiresIn, "/api/verification-request/link?request-id=%s");
        return VerificationSession.generate(request);
    }

    @Override
    public SignedAgeCertificate createSignedAgeCertificate(
            String accountId, AuthMatchData authData, VerificationSession session) {
        AesGcmEncryptionPackage authToken = authDataEncryptor.encrypt(authData, session.authKey());
        return createSignedAgeCertificate(accountId, authToken, session);
    }

    @Override
    public SignedAgeCertificate createSignedAgeCertificate(
            String accountId, AesGcmEncryptionPackage authToken, VerificationSession session) {
        VerifiedUser user = getVerificationState(accountId).verifiedUser();
        AgeCertificate certificate = AgeCertificate.of(session.verificationRequest(), user, authToken);
        return certificateSigner.sign(certificate);
    }
}
