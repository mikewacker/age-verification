package org.example.age.api.def.avs;

import org.example.age.api.base.Dispatcher;
import org.example.age.api.base.Sender;
import org.example.age.api.def.common.AuthMatchData;
import org.example.age.api.def.common.VerificationState;
import org.example.age.data.certificate.SignedAgeCertificate;
import org.example.age.data.certificate.VerificationRequest;
import org.example.age.data.certificate.VerificationSession;
import org.example.age.data.crypto.SecureId;

/** Asynchronous API for the age verification service. */
public interface AvsApi {

    /** Gets the {@link VerificationState} for a person. */
    void getVerificationState(Sender.Value<VerificationState> sender, String accountId, Dispatcher dispatcher)
            throws Exception;

    /** Creates a {@link VerificationSession} for a site. */
    void createVerificationSession(Sender.Value<VerificationSession> sender, String siteId, Dispatcher dispatcher)
            throws Exception;

    /** Links a pending {@link VerificationRequest} to a person. */
    void linkVerificationRequest(Sender.StatusCode sender, String accountId, SecureId requestId, Dispatcher dispatcher)
            throws Exception;

    /**
     * Sends a {@link SignedAgeCertificate} for a person,
     * using the pending {@link VerificationRequest} that is linked to that person.
     */
    void sendAgeCertificate(Sender.StatusCode sender, String accountId, AuthMatchData authData, Dispatcher dispatcher)
            throws Exception;
}
