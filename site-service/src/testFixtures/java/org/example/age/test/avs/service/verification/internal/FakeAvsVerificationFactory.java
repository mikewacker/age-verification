package org.example.age.test.avs.service.verification.internal;

import org.example.age.common.api.data.AuthMatchData;
import org.example.age.data.certificate.SignedAgeCertificate;
import org.example.age.data.certificate.VerificationSession;

/** Factory that creates {@link VerificationSession}'s and {@link SignedAgeCertificate}'s. */
public interface FakeAvsVerificationFactory {

    /** Creates a {@link VerificationSession} for the site. */
    VerificationSession createVerificationSession(String siteId);

    /** Creates a {@link SignedAgeCertificate} for the account. */
    SignedAgeCertificate createSignedAgeCertificate(
            String accountId, AuthMatchData authData, VerificationSession session);
}
