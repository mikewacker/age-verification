package org.example.age.service.verification.internal.avs;

import java.time.Duration;
import org.example.age.api.def.common.AuthMatchData;
import org.example.age.api.def.common.VerificationState;
import org.example.age.data.certificate.SignedAgeCertificate;
import org.example.age.data.certificate.VerificationSession;
import org.example.age.data.crypto.AesGcmEncryptionPackage;

/** Factory that creates {@link VerificationSession}'s and {@link SignedAgeCertificate}'s. */
public interface FakeAvsVerificationFactory {

    /** Gets the {@link VerificationState} for a person. */
    VerificationState getVerificationState(String accountId);

    /** Creates a {@link VerificationSession} for a site. */
    VerificationSession createVerificationSession(String siteId);

    /** Creates a {@link VerificationSession} for a site. */
    VerificationSession createVerificationSession(String siteId, Duration expiresIn);

    /** Creates a {@link SignedAgeCertificate} for a person. */
    SignedAgeCertificate createSignedAgeCertificate(
            String accountId, AuthMatchData authData, VerificationSession session);

    /** Creates a {@link SignedAgeCertificate} for a person. */
    SignedAgeCertificate createSignedAgeCertificate(
            String accountId, AesGcmEncryptionPackage authToken, VerificationSession session);
}
