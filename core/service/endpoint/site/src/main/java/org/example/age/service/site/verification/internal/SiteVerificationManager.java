package org.example.age.service.site.verification.internal;

import org.example.age.api.base.Dispatcher;
import org.example.age.api.def.common.AuthMatchData;
import org.example.age.api.def.common.VerificationState;
import org.example.age.data.certificate.SignedAgeCertificate;
import org.example.age.data.certificate.VerificationSession;

/** Synchronously manages the age verification process for accounts. */
public interface SiteVerificationManager {

    /** Gets the {@link VerificationState} for an account. */
    VerificationState getVerificationState(String accountId);

    /** Called when a {@link VerificationSession} is received for an account, returning a status code. */
    int onVerificationSessionReceived(
            String accountId, AuthMatchData authData, VerificationSession session, Dispatcher dispatcher);

    /** Called when a {@link SignedAgeCertificate} is received, returning a status code. */
    int onSignedAgeCertificateReceived(SignedAgeCertificate signedCertificate);
}
