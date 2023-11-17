package org.example.age.site.service.verification.internal;

import org.example.age.api.Dispatcher;
import org.example.age.common.api.data.AuthMatchData;
import org.example.age.data.certificate.SignedAgeCertificate;
import org.example.age.data.certificate.VerificationSession;

/** Synchronously manages the age verification process for accounts. */
public interface VerificationManager {

    /** Called when a {@link VerificationSession} is received for an account, returning a status code. */
    int onVerificationSessionReceived(
            String accountId, AuthMatchData authData, VerificationSession session, Dispatcher dispatcher);

    /** Called when a {@link SignedAgeCertificate} is received, returning a status code. */
    int onSignedAgeCertificateReceived(SignedAgeCertificate signedCertificate);
}
