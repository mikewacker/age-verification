package org.example.age.service.verification.internal.avs;

import org.example.age.api.base.Dispatcher;
import org.example.age.api.base.HttpOptional;
import org.example.age.api.def.common.AuthMatchData;
import org.example.age.api.def.common.VerificationState;
import org.example.age.data.certificate.SignedAgeCertificate;
import org.example.age.data.certificate.VerificationRequest;
import org.example.age.data.certificate.VerificationSession;
import org.example.age.data.crypto.SecureId;

/** Synchronously manages the age verification process for people. */
public interface AvsVerificationManager {

    /** Gets the {@link VerificationState} for a person. */
    VerificationState getVerificationState(String accountId);

    /** Creates a {@link VerificationSession} for a site. */
    HttpOptional<VerificationSession> createVerificationSession(String siteId, Dispatcher dispatcher);

    /** Links a pending {@link VerificationRequest} to a person. */
    int linkVerificationRequest(String accountId, SecureId requestId, Dispatcher dispatcher);

    /**
     * Creates a {@link SignedAgeCertificate} for a person,
     * using the pending {@link VerificationRequest} that is linked to that person.
     */
    HttpOptional<SignedAgeCertificate> createAgeCertificate(String accountId, AuthMatchData authData);
}
