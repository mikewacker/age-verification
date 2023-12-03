package org.example.age.data.certificate;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;
import org.example.age.api.ApiStyle;
import org.example.age.data.crypto.DigitalSignature;
import org.example.age.data.mapper.DataMapper;
import org.immutables.value.Value;

/**
 * Age certificate that is signed to verify the sender's identity.
 *
 * <p>Only {@code Ed25519} keys are supported.</p>
 */
@Value.Immutable
@ApiStyle
@JsonDeserialize(as = ImmutableSignedAgeCertificate.class)
public interface SignedAgeCertificate {

    /** Creates a signed age certificate. */
    static SignedAgeCertificate of(AgeCertificate certificate, DigitalSignature signature) {
        return ImmutableSignedAgeCertificate.builder()
                .ageCertificate(certificate)
                .signature(signature)
                .build();
    }

    /** Signs the age certificate. */
    static SignedAgeCertificate sign(AgeCertificate certificate, PrivateKey privateKey) {
        byte[] rawCertificate = serialize(certificate);
        DigitalSignature signature = DigitalSignature.sign(rawCertificate, privateKey);
        return of(certificate, signature);
    }

    /** Age certificate. */
    AgeCertificate ageCertificate();

    /** Signature for the age certificate. */
    DigitalSignature signature();

    /** Verifies the signature against the age certificate, returning whether verification succeeded. */
    default boolean verify(PublicKey publicKey) {
        byte[] rawCertificate = serialize(ageCertificate());
        return signature().verify(rawCertificate, publicKey);
    }

    /** Serializes an {@link AgeCertificate}. */
    private static byte[] serialize(AgeCertificate certificate) {
        try {
            return DataMapper.get().writeValueAsBytes(certificate);
        } catch (IOException e) {
            throw new RuntimeException("serialization failed", e);
        }
    }
}
