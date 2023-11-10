package org.example.age.avs.api;

import org.example.age.common.api.CodeSender;
import org.example.age.common.api.ExchangeExecutors;
import org.example.age.common.api.JsonSender;
import org.example.age.common.api.data.auth.AuthMatchData;
import org.example.age.data.SecureId;
import org.example.age.data.certificate.AgeCertificate;
import org.example.age.data.certificate.VerificationRequest;
import org.example.age.data.certificate.VerificationSession;

/** Asynchronous API for the age verification service. */
public interface AvsApi {

    /** Creates a {@link VerificationSession} for a site. */
    void createVerificationSession(JsonSender<VerificationSession> sender, String siteId, ExchangeExecutors executors);

    /** Links a pending {@link VerificationRequest} to an account. */
    void linkVerificationRequest(CodeSender sender, String accountId, SecureId requestId, ExchangeExecutors executors);

    /** Sends an {@link AgeCertificate} for the pending {@link VerificationRequest} that is linked to the account. */
    void sendAgeCertificate(CodeSender sender, String accountId, AuthMatchData authData, ExchangeExecutors executors);
}
