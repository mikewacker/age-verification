package org.example.age.site.endpoint;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotFoundException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.CompletionStage;
import org.example.age.common.api.AgeCertificate;
import org.example.age.common.api.SignedAgeCertificate;
import org.example.age.common.api.VerificationRequest;
import org.example.age.site.spi.AgeCertificateVerifier;

/** Validates signed age certificates. */
@Singleton
final class AgeCertificateValidator {

    private final AgeCertificateVerifier verifier;
    private final SiteEndpointConfig config;

    @Inject
    public AgeCertificateValidator(AgeCertificateVerifier verifier, SiteEndpointConfig config) {
        this.verifier = verifier;
        this.config = config;
    }

    /** Validates a signed age certificate, or throws an error. */
    public CompletionStage<AgeCertificate> validate(SignedAgeCertificate signedAgeCertificate) {
        return verifier.verify(signedAgeCertificate).thenApply(this::validate);
    }

    /** Validates an age certificate, or throws an error. */
    private AgeCertificate validate(AgeCertificate ageCertificate) {
        VerificationRequest request = ageCertificate.getRequest();
        if (!request.getSiteId().equals(config.id())) {
            throw new ForbiddenException();
        }

        if (request.getExpiration().isBefore(OffsetDateTime.now(ZoneOffset.UTC))) {
            throw new NotFoundException();
        }

        return ageCertificate;
    }
}
