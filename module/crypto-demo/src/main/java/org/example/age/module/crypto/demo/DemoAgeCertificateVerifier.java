package org.example.age.module.crypto.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.ServerErrorException;
import java.security.Signature;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import org.example.age.api.AgeCertificate;
import org.example.age.api.SignedAgeCertificate;
import org.example.age.api.crypto.SignatureData;
import org.example.age.module.common.LiteEnv;
import org.example.age.module.crypto.demo.keys.SiteKeysConfig;
import org.example.age.service.module.crypto.AgeCertificateVerifier;

/**
 * Implementation of {@link AgeCertificateVerifier}.
 * Loads keys from configuration; it suffices to say that a production application should NOT do this.
 */
@Singleton
final class DemoAgeCertificateVerifier implements AgeCertificateVerifier {

    private final SiteKeysConfig config;
    private final ObjectMapper mapper;

    @Inject
    public DemoAgeCertificateVerifier(SiteKeysConfig config, LiteEnv liteEnv) {
        this.config = config;
        this.mapper = liteEnv.jsonMapper();
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
            verifier.initVerify(config.signingJca());
            return verifier;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
