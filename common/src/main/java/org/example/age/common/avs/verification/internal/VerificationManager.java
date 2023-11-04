package org.example.age.common.avs.verification.internal;

import io.undertow.server.HttpServerExchange;
import org.example.age.common.api.HttpOptional;
import org.example.age.data.SecureId;
import org.example.age.data.certificate.AgeCertificate;
import org.example.age.data.certificate.VerificationSession;

/** Manages the age verification process for accounts. */
public interface VerificationManager {

    /** Creates a {@link VerificationSession} for the site. */
    HttpOptional<VerificationSession> createVerificationSession(String siteId, HttpServerExchange exchange);

    /** Links a pending verification request to an account. */
    int linkVerificationRequest(String accountId, SecureId requestId, HttpServerExchange exchange);

    /** Creates an {@link AgeCertificate} for an account from a pending verification request that is linked to it. */
    HttpOptional<Verification> createAgeCertificate(String accountId, HttpServerExchange exchange);
}
