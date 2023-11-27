package org.example.age.test.avs.service.verification.internal;

import org.example.age.common.api.data.AuthMatchData;
import org.example.age.data.certificate.SignedAgeCertificate;
import org.example.age.data.certificate.VerificationSession;
import org.example.age.data.user.VerifiedUser;

/** Factory that creates {@link VerificationSession}'s and {@link SignedAgeCertificate}'s. */
public interface FakeAvsVerificationFactory {

    /** Creates a {@link VerificationSession} for the site. */
    VerificationSession createVerificationSession(String siteId);

    /** Creates a {@link SignedAgeCertificate} for the {@link VerifiedUser}. */
    SignedAgeCertificate createSignedAgeCertificate(
            VerificationSession session, VerifiedUser user, AuthMatchData authData);
}
