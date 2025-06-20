package org.example.age.api.testing;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.ECGenParameterSpec;
import org.example.age.api.AgeCertificate;
import org.example.age.api.DigitalSignature;
import org.example.age.api.SignedAgeCertificate;
import org.example.age.api.crypto.SignatureData;
import org.example.age.common.testing.TestObjectMapper;

/** Test utilities for signing and verifying age certificates. Uses a singleton key pair. */
public final class TestSignatures {

    private static final KeyPair keyPair = generateKeyPair();

    /** Gets the NIST P-256 key pair. */
    public static KeyPair getKeyPair() {
        return keyPair;
    }

    /** Signs an age certificate. */
    public static SignedAgeCertificate sign(AgeCertificate ageCertificate) {
        Signature signer = createSigner(keyPair.getPrivate());
        SignatureData data = SignatureData.sign(ageCertificate, TestObjectMapper.get(), signer);
        DigitalSignature signature =
                DigitalSignature.builder().algorithm("secp256r1").data(data).build();
        return SignedAgeCertificate.builder()
                .ageCertificate(ageCertificate)
                .signature(signature)
                .build();
    }

    /** Signs an age certificate with an invalid signature. */
    public static SignedAgeCertificate signInvalid(AgeCertificate ageCertificate, String algorithm) {
        DigitalSignature signature = DigitalSignature.builder()
                .algorithm(algorithm)
                .data(SignatureData.fromString(""))
                .build();
        return SignedAgeCertificate.builder()
                .ageCertificate(ageCertificate)
                .signature(signature)
                .build();
    }

    /** Verifies a signed age certificate. */
    public static AgeCertificate verify(SignedAgeCertificate signedAgeCertificate) {
        Signature verifier = createVerifier(keyPair.getPublic());
        AgeCertificate ageCertificate = signedAgeCertificate.getAgeCertificate();
        SignatureData data = signedAgeCertificate.getSignature().getData();
        data.verify(ageCertificate, TestObjectMapper.get(), verifier);
        return ageCertificate;
    }

    /** Generates a NIST P-256 key pair. */
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
    private static Signature createSigner(PrivateKey privateKey) {
        try {
            Signature signer = Signature.getInstance("SHA256withECDSA");
            signer.initSign(privateKey);
            return signer;
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    /** Creates a verifier from the public key. */
    private static Signature createVerifier(PublicKey publicKey) {
        try {
            Signature verifier = Signature.getInstance("SHA256withECDSA");
            verifier.initVerify(publicKey);
            return verifier;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private TestSignatures() {} // static class
}
