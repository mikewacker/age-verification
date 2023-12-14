package org.example.age.api.avs;

import org.example.age.api.base.Dispatcher;
import org.example.age.api.base.Sender;
import org.example.age.api.common.AuthMatchData;
import org.example.age.api.common.VerificationState;
import org.example.age.data.certificate.AgeCertificate;
import org.example.age.data.certificate.VerificationRequest;
import org.example.age.data.certificate.VerificationSession;
import org.example.age.data.crypto.SecureId;

/** Asynchronous API for the age verification service. */
public interface AvsApi {

    /** Gets the {@link VerificationState} for an account. */
    void getVerificationState(Sender.Value<VerificationState> sender, String accountId, Dispatcher dispatcher)
            throws Exception;

    /** Creates a {@link VerificationSession} for a site. */
    void createVerificationSession(Sender.Value<VerificationSession> sender, String siteId, Dispatcher dispatcher)
            throws Exception;

    /** Links a pending {@link VerificationRequest} to an account. */
    void linkVerificationRequest(Sender.StatusCode sender, String accountId, SecureId requestId, Dispatcher dispatcher)
            throws Exception;

    /** Sends an {@link AgeCertificate} for the pending {@link VerificationRequest} that is linked to the account. */
    void sendAgeCertificate(Sender.StatusCode sender, String accountId, AuthMatchData authData, Dispatcher dispatcher)
            throws Exception;
}