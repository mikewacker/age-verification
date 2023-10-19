package org.example.age.common.site.auth.internal;

import io.undertow.server.HttpServerExchange;
import org.example.age.common.auth.AuthMatchData;
import org.example.age.data.certificate.AgeCertificate;
import org.example.age.data.certificate.VerificationSession;

/** Adds an authentication check to the age verification process using {@link AuthMatchData}. */
public interface AuthManager {

    /** Called when a {@link VerificationSession} is received as part of an exchange, returning a status code. */
    int onVerificationSessionReceived(VerificationSession session, HttpServerExchange exchange);

    /** Called when an {@link AgeCertificate} is received, returning a status code. */
    int onAgeCertificateReceived(AgeCertificate certificate);
}
