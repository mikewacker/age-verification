package org.example.age.service.testing;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.NotAuthorizedException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Signature;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.ECGenParameterSpec;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import org.example.age.api.AgeCertificate;
import org.example.age.api.DigitalSignature;
import org.example.age.api.SignedAgeCertificate;
import org.example.age.api.crypto.SignatureData;
import org.example.age.service.api.AgeCertificateSigner;
import org.example.age.service.api.AgeCertificateVerifier;

/** Fake implementation of {@link AgeCertificateSigner} and {@link AgeCertificateVerifier}. */
@Singleton
final class FakeAgeCertificateSignerVerifier implements AgeCertificateSigner, AgeCertificateVerifier {

    private static final KeyPair keyPair = generateKeyPair();

    private final ObjectMapper mapper;

    @Inject
    public FakeAgeCertificateSignerVerifier(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public CompletionStage<SignedAgeCertificate> sign(AgeCertificate ageCertificate) {
        try {
            Signature signer = createSigner();
            SignatureData data = SignatureData.sign(ageCertificate, mapper, signer);
            DigitalSignature signature =
                    DigitalSignature.builder().algorithm("secp256r1").data(data).build();
            SignedAgeCertificate signedAgeCertificate = SignedAgeCertificate.builder()
                    .ageCertificate(ageCertificate)
                    .signature(signature)
                    .build();
            return CompletableFuture.completedFuture(signedAgeCertificate);
        } catch (Exception e) {
            return CompletableFuture.failedFuture(new InternalServerErrorException());
        }
    }

    @Override
    public CompletionStage<AgeCertificate> verify(SignedAgeCertificate signedAgeCertificate) {
        try {
            SignatureData data = signedAgeCertificate.getSignature().getData();
            AgeCertificate ageCertificate = signedAgeCertificate.getAgeCertificate();
            Signature verifier = createVerifier();
            try {
                data.verify(ageCertificate, mapper, verifier);
            } catch (RuntimeException e) {
                return CompletableFuture.failedFuture(new NotAuthorizedException(e));
            }

            return CompletableFuture.completedFuture(ageCertificate);
        } catch (Exception e) {
            return CompletableFuture.failedFuture(new InternalServerErrorException());
        }
    }

    /** Generates a key pair. */
    private static KeyPair generateKeyPair() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC");
            AlgorithmParameterSpec nistP256Spec = new ECGenParameterSpec("secp256r1");
            keyPairGenerator.initialize(nistP256Spec);
            return keyPairGenerator.generateKeyPair();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /** Creates a signer from the private key. */
    private static Signature createSigner() throws Exception {
        Signature signer = Signature.getInstance("SHA256withECDSA");
        signer.initSign(keyPair.getPrivate());
        return signer;
    }

    /** Creates a verifier from the public key. */
    private static Signature createVerifier() throws Exception {
        Signature verifier = Signature.getInstance("SHA256withECDSA");
        verifier.initVerify(keyPair.getPublic());
        return verifier;
    }
}
