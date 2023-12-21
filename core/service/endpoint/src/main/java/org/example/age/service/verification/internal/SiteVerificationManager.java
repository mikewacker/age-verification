package org.example.age.service.verification.internal;

import org.example.age.api.base.HttpOptional;
import org.example.age.api.base.ScheduledExecutor;
import org.example.age.api.def.AuthMatchData;
import org.example.age.api.def.VerificationState;
import org.example.age.data.certificate.SignedAgeCertificate;
import org.example.age.data.certificate.VerificationSession;

/** Synchronously manages the age verification process for accounts. */
public interface SiteVerificationManager {

    /** Gets the {@link VerificationState} for an account. */
    VerificationState getVerificationState(String accountId);

    /** Called when a {@link VerificationSession} is received for an account. */
    int onVerificationSessionReceived(
            String accountId, AuthMatchData authData, VerificationSession session, ScheduledExecutor executor);

    /** Called when a {@link SignedAgeCertificate} is received, returning a redirect path. */
    HttpOptional<String> onAgeCertificateReceived(SignedAgeCertificate signedCertificate);
}