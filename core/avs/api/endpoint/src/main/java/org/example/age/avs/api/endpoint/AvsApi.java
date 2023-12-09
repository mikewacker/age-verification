package org.example.age.avs.api.endpoint;

import org.example.age.api.base.Dispatcher;
import org.example.age.api.base.StatusCodeSender;
import org.example.age.api.base.ValueSender;
import org.example.age.common.api.data.AuthMatchData;
import org.example.age.common.api.data.VerificationState;
import org.example.age.data.certificate.AgeCertificate;
import org.example.age.data.certificate.VerificationRequest;
import org.example.age.data.certificate.VerificationSession;
import org.example.age.data.crypto.SecureId;

/** Asynchronous API for the age verification service. */
public interface AvsApi {

    /** Gets the {@link VerificationState} for an account. */
    void getVerificationState(ValueSender<VerificationState> sender, String accountId, Dispatcher dispatcher);

    /** Creates a {@link VerificationSession} for a site. */
    void createVerificationSession(ValueSender<VerificationSession> sender, String siteId, Dispatcher dispatcher);

    /** Links a pending {@link VerificationRequest} to an account. */
    void linkVerificationRequest(StatusCodeSender sender, String accountId, SecureId requestId, Dispatcher dispatcher);

    /** Sends an {@link AgeCertificate} for the pending {@link VerificationRequest} that is linked to the account. */
    void sendAgeCertificate(StatusCodeSender sender, String accountId, AuthMatchData authData, Dispatcher dispatcher);
}
