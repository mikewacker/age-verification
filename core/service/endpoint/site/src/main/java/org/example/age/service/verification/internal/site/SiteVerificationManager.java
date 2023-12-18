package org.example.age.service.verification.internal.site;

import org.example.age.api.base.ScheduledExecutor;
import org.example.age.api.def.common.AuthMatchData;
import org.example.age.api.def.common.VerificationState;
import org.example.age.data.certificate.SignedAgeCertificate;
import org.example.age.data.certificate.VerificationSession;

/** Synchronously manages the age verification process for accounts. */
public interface SiteVerificationManager {

    /** Gets the {@link VerificationState} for an account. */
    VerificationState getVerificationState(String accountId);

    /** Called when a {@link VerificationSession} is received for an account. */
    int onVerificationSessionReceived(
            String accountId, AuthMatchData authData, VerificationSession session, ScheduledExecutor executor);

    /** Called when a {@link SignedAgeCertificate} is received. */
    int onAgeCertificateReceived(SignedAgeCertificate signedCertificate);
}
