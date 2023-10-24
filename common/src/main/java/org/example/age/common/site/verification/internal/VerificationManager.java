package org.example.age.common.site.verification.internal;

import io.undertow.server.HttpServerExchange;
import org.example.age.data.certificate.AgeCertificate;
import org.example.age.data.certificate.VerificationSession;

/** Manages the age verification process for accounts. */
public interface VerificationManager {

    /** Called when a {@link VerificationSession} is received for an account as part of an exchange. */
    void onVerificationSessionReceived(String accountId, VerificationSession session, HttpServerExchange exchange);

    /** Called when an {@link AgeCertificate} is received, returning a status code. */
    int onAgeCertificateReceived(AgeCertificate certificate);
}
