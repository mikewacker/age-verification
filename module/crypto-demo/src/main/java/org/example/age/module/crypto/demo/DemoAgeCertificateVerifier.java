package org.example.age.module.crypto.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.ServerErrorException;
import java.security.PublicKey;
import java.security.Signature;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import org.example.age.api.AgeCertificate;
import org.example.age.api.SignedAgeCertificate;
import org.example.age.api.crypto.SignatureData;
import org.example.age.service.api.crypto.AgeCertificateVerifier;

@Singleton
final class DemoAgeCertificateVerifier implements AgeCertificateVerifier {

    private final SiteKeysConfig config;
    private final ObjectMapper mapper;

    @Inject
    public DemoAgeCertificateVerifier(SiteKeysConfig config, ObjectMapper mapper) {
        this.config = config;
        this.mapper = mapper;
    }

    @Override
    public CompletionStage<AgeCertificate> verify(SignedAgeCertificate signedAgeCertificate) {
        if (!signedAgeCertificate.getSignature().getAlgorithm().equals("secp256r1")) {
            return CompletableFuture.failedFuture(new ServerErrorException(501));
        }

        SignatureData data = signedAgeCertificate.getSignature().getData();
        AgeCertificate ageCertificate = signedAgeCertificate.getAgeCertificate();
        Signature verifier = createVerifier();
        try {
            data.verify(ageCertificate, mapper, verifier);
        } catch (RuntimeException e) {
            return CompletableFuture.failedFuture(new NotAuthorizedException("signature verification failed"));
        }

        return CompletableFuture.completedFuture(ageCertificate);
    }

    /** Creates a {@link Signature} object for verifying. */
    private Signature createVerifier() {
        try {
            Signature verifier = Signature.getInstance("SHA256withECDSA");
            PublicKey publicKey = NistP256Keys.toPublicKey(config.signing());
            verifier.initVerify(publicKey);
            return verifier;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
