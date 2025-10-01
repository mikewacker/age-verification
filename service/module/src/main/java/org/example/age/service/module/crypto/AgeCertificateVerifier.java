package org.example.age.service.module.crypto;

import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.ServerErrorException;
import java.util.concurrent.CompletionStage;
import org.example.age.common.api.AgeCertificate;
import org.example.age.common.api.SignedAgeCertificate;

/**
 * Verifies a signed age certificate.
 * <p>
 * Throws {@link NotAuthorizedException} if verification fails,
 * or {@link ServerErrorException} (501) if the algorithm is not supported.
 */
@FunctionalInterface
public interface AgeCertificateVerifier {

    CompletionStage<AgeCertificate> verify(SignedAgeCertificate signedAgeCertificate);
}
