package org.example.age.common.verification;

import io.undertow.server.HttpServerExchange;
import org.example.age.certificate.AgeCertificate;
import org.example.age.certificate.VerificationSession;

/** Manages {@link VerificationState}'s for accounts. */
public interface VerificationStateManager {

    /** Gets the {@link VerificationState}. */
    VerificationState getVerificationState(HttpServerExchange exchange);

    /** Called when a {@link VerificationSession} is received, returning a status code. */
    int onVerificationSessionReceived(VerificationSession session, HttpServerExchange exchange);

    /** Called when an {@link AgeCertificate} is received, returning a status code. */
    int onAgeCertificateReceived(AgeCertificate certificate);
}
