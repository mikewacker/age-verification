package org.example.age.module.crypto.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.security.Signature;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import org.example.age.common.api.AgeCertificate;
import org.example.age.common.api.DigitalSignature;
import org.example.age.common.api.SignedAgeCertificate;
import org.example.age.common.api.crypto.SignatureData;
import org.example.age.common.env.LiteEnv;
import org.example.age.module.crypto.demo.keys.AvsKeysConfig;
import org.example.age.service.module.crypto.AgeCertificateSigner;

/**
 * Implementation of {@link AgeCertificateSigner}.
 * Loads keys from configuration; it suffices to say that a production application should NOT do this.
 */
@Singleton
final class DemoAgeCertificateSigner implements AgeCertificateSigner {

    private final AvsKeysConfig config;
    private final ObjectMapper mapper;

    @Inject
    public DemoAgeCertificateSigner(AvsKeysConfig config, LiteEnv liteEnv) {
        this.config = config;
        this.mapper = liteEnv.jsonMapper();
    }

    @Override
    public CompletionStage<SignedAgeCertificate> sign(AgeCertificate ageCertificate) {
        Signature signer = createSigner();
        SignatureData data = SignatureData.sign(ageCertificate, mapper, signer);
        DigitalSignature signature =
                DigitalSignature.builder().algorithm("secp256r1").data(data).build();
        SignedAgeCertificate signedAgeCertificate = SignedAgeCertificate.builder()
                .ageCertificate(ageCertificate)
                .signature(signature)
                .build();
        return CompletableFuture.completedFuture(signedAgeCertificate);
    }

    /** Creates a {@link Signature} object for signing. */
    private Signature createSigner() {
        try {
            Signature signing = Signature.getInstance("SHA256withECDSA");
            signing.initSign(config.signingJca());
            return signing;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
