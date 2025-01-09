package org.example.age.module.crypto.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.security.PrivateKey;
import java.security.Signature;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import org.example.age.api.AgeCertificate;
import org.example.age.api.DigitalSignature;
import org.example.age.api.SignedAgeCertificate;
import org.example.age.api.crypto.SignatureData;
import org.example.age.service.api.crypto.AgeCertificateSigner;

/** Implementation of {@link AgeCertificateSigner}. */
@Singleton
final class DemoAgeCertificateSigner implements AgeCertificateSigner {

    private final AvsKeysConfig config;
    private final ObjectMapper mapper;

    @Inject
    public DemoAgeCertificateSigner(AvsKeysConfig config, ObjectMapper mapper) {
        this.config = config;
        this.mapper = mapper;
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
            PrivateKey privateKey = NistP256Keys.toPrivateKey(config.signing());
            signing.initSign(privateKey);
            return signing;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
