package org.example.age.site.provider.certificateverifier.demo;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.ServerErrorException;
import java.security.PublicKey;
import java.security.Signature;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import org.example.age.common.api.AgeCertificate;
import org.example.age.common.api.SignedAgeCertificate;
import org.example.age.common.api.crypto.SignatureData;
import org.example.age.common.env.JsonMapper;
import org.example.age.common.provider.signingkey.demo.EcPublicKeyConfig;
import org.example.age.common.provider.signingkey.demo.NistP256KeyFactory;
import org.example.age.site.spi.AgeCertificateVerifier;

/** Implementation of {@link AgeCertificateVerifier}. Uses a NIST P-256 key. */
@Singleton
final class DemoAgeCertificateVerifier implements AgeCertificateVerifier {

    private final PublicKey publicKey;
    private final JsonMapper mapper;

    @Inject
    public DemoAgeCertificateVerifier(EcPublicKeyConfig config, JsonMapper mapper) {
        publicKey = NistP256KeyFactory.createPublic(config.w());
        this.mapper = mapper;
    }

    @Override
    public CompletionStage<AgeCertificate> verify(SignedAgeCertificate signedAgeCertificate) {
        if (!signedAgeCertificate.getSignature().getAlgorithm().equals("secp256r1")) {
            return CompletableFuture.failedFuture(new ServerErrorException(501));
        }

        Signature verifier = createVerifier();
        AgeCertificate ageCertificate = signedAgeCertificate.getAgeCertificate();
        String ageCertificateJson = mapper.serialize(ageCertificate);
        SignatureData data = signedAgeCertificate.getSignature().getData();
        try {
            data.verify(verifier, ageCertificateJson);
        } catch (RuntimeException e) {
            return CompletableFuture.failedFuture(new NotAuthorizedException("signature verification failed"));
        }

        return CompletableFuture.completedFuture(ageCertificate);
    }

    /** Creates a {@link Signature} object for verifying. */
    private Signature createVerifier() {
        try {
            Signature verifier = Signature.getInstance("SHA256withECDSA");
            verifier.initVerify(publicKey);
            return verifier;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
