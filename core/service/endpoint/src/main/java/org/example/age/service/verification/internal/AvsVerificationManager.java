package org.example.age.service.verification.internal;

import org.example.age.api.base.HttpOptional;
import org.example.age.api.base.ScheduledExecutor;
import org.example.age.api.def.AuthMatchData;
import org.example.age.api.def.VerificationState;
import org.example.age.data.certificate.SignedAgeCertificate;
import org.example.age.data.certificate.VerificationRequest;
import org.example.age.data.certificate.VerificationSession;
import org.example.age.data.crypto.SecureId;

/** Synchronously manages the age verification process for people. */
public interface AvsVerificationManager {

    /** Gets the {@link VerificationState} for a person. */
    VerificationState getVerificationState(String accountId);

    /** Creates a {@link VerificationSession} for a site. */
    HttpOptional<VerificationSession> createVerificationSession(String siteId, ScheduledExecutor executor);

    /** Links a pending {@link VerificationRequest} to a person. */
    int linkVerificationRequest(String accountId, SecureId requestId, ScheduledExecutor executor);

    /**
     * Creates a {@link SignedAgeCertificate} for a person,
     * using the pending {@link VerificationRequest} that is linked to that person.
     */
    HttpOptional<SignedAgeCertificate> createAgeCertificate(String accountId, AuthMatchData authData);
}
