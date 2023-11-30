package org.example.age.test.avs.service.verification.internal;

import java.time.Duration;
import java.util.List;
import java.util.Map;
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
import org.example.age.data.crypto.SecureId;
import org.example.age.data.user.VerifiedUser;

@Singleton
final class FakeAvsVerificationFactoryImpl implements FakeAvsVerificationFactory {

    private final AgeCertificateSigner certificateSigner;
    private final AuthMatchDataEncryptor authDataEncryptor;

    private final Map<String, VerifiedUser> users = populateAccounts();

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
            String accountId, AuthMatchData authData, VerificationSession session) {
        VerifiedUser user = loadVerifiedUser(accountId);
        AesGcmEncryptionPackage authToken = authDataEncryptor.encrypt(authData, session.authKey());
        AgeCertificate certificate = AgeCertificate.of(session.verificationRequest(), user, authToken);
        return certificateSigner.sign(certificate);
    }

    /** Populates preset accounts. */
    private static Map<String, VerifiedUser> populateAccounts() {
        VerifiedUser parent = VerifiedUser.of(SecureId.generate(), 40);
        VerifiedUser child = VerifiedUser.of(SecureId.generate(), 13, List.of(parent.pseudonym()));
        return Map.of("John Smith", parent, "Billy Smith", child);
    }

    /** Loads the {@link VerifiedUser} for an account. */
    private VerifiedUser loadVerifiedUser(String accountId) {
        VerifiedUser user = users.get(accountId);
        if (user == null) {
            String message = String.format("account is not verified: %s", accountId);
            throw new IllegalArgumentException(message);
        }
        return user;
    }
}
