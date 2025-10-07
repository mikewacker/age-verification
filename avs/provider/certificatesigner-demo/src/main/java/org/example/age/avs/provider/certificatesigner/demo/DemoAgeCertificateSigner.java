package org.example.age.avs.provider.certificatesigner.demo;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.security.PrivateKey;
import java.security.Signature;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import org.example.age.avs.spi.AgeCertificateSigner;
import org.example.age.common.api.AgeCertificate;
import org.example.age.common.api.DigitalSignature;
import org.example.age.common.api.SignedAgeCertificate;
import org.example.age.common.api.crypto.SignatureData;
import org.example.age.common.env.JsonMapper;
import org.example.age.common.provider.signingkey.demo.EcPrivateKeyConfig;
import org.example.age.common.provider.signingkey.demo.NistP256KeyFactory;

/** Implementation of {@link AgeCertificateSigner}. Uses a NIST P-256 key. */
@Singleton
final class DemoAgeCertificateSigner implements AgeCertificateSigner {

    private final PrivateKey privateKey;
    private final JsonMapper mapper;

    @Inject
    public DemoAgeCertificateSigner(EcPrivateKeyConfig config, JsonMapper mapper) {
        privateKey = NistP256KeyFactory.createPrivate(config.s());
        this.mapper = mapper;
    }

    @Override
    public CompletionStage<SignedAgeCertificate> sign(AgeCertificate ageCertificate) {
        Signature signer = createSigner();
        String ageCertificateJson = mapper.serialize(ageCertificate);
        SignatureData data = SignatureData.sign(signer, ageCertificateJson);
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
            signing.initSign(privateKey);
            return signing;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
