package org.example.age.test.avs.service.verification.internal;

import java.time.Duration;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.example.age.common.api.data.AuthMatchData;
import org.example.age.common.service.crypto.internal.AgeCertificateSigner;
import org.example.age.common.service.crypto.internal.AuthMatchDataEncryptor;
import org.example.age.data.certificate.AgeCertificate;
import org.example.age.data.certificate.SignedAgeCertificate;
import org.example.age.data.certificate.VerificationRequest;
import org.example.age.data.certificate.VerificationSession;
import org.example.age.data.crypto.AesGcmEncryptionPackage;
import org.example.age.data.user.VerifiedUser;

@Singleton
final class FakeAvsVerificationFactoryImpl implements FakeAvsVerificationFactory {

    private final AgeCertificateSigner certificateSigner;
    private final AuthMatchDataEncryptor authDataEncryptor;

    @Inject
    public FakeAvsVerificationFactoryImpl(
            AgeCertificateSigner certificateSigner, AuthMatchDataEncryptor authDataEncryptor) {
        this.certificateSigner = certificateSigner;
        this.authDataEncryptor = authDataEncryptor;
    }

    @Override
    public VerificationSession createVerificationSession(String siteId) {
        VerificationRequest request = VerificationRequest.generateForSite(siteId, Duration.ofMinutes(5));
        return VerificationSession.create(request);
    }

    @Override
    public SignedAgeCertificate createSignedAgeCertificate(
            VerificationSession session, VerifiedUser user, AuthMatchData authData) {
        AesGcmEncryptionPackage authToken = authDataEncryptor.encrypt(authData, session.authKey());
        AgeCertificate certificate = AgeCertificate.of(session.verificationRequest(), user, authToken);
        return certificateSigner.sign(certificate);
    }
}
