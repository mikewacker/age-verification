package org.example.age.avs.api;

import org.example.age.api.Dispatcher;
import org.example.age.api.JsonSender;
import org.example.age.api.StatusCodeSender;
import org.example.age.common.api.data.AuthMatchData;
import org.example.age.data.certificate.AgeCertificate;
import org.example.age.data.certificate.VerificationRequest;
import org.example.age.data.certificate.VerificationSession;
import org.example.age.data.crypto.SecureId;

/** Asynchronous API for the age verification service. */
public interface AvsApi {

    /** Creates a {@link VerificationSession} for a site. */
    void createVerificationSession(JsonSender<VerificationSession> sender, String siteId, Dispatcher dispatcher);

    /** Links a pending {@link VerificationRequest} to an account. */
    void linkVerificationRequest(StatusCodeSender sender, String accountId, SecureId requestId, Dispatcher dispatcher);

    /** Sends an {@link AgeCertificate} for the pending {@link VerificationRequest} that is linked to the account. */
    void sendAgeCertificate(StatusCodeSender sender, String accountId, AuthMatchData authData, Dispatcher dispatcher);
}
