package org.example.age.data.certificate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.security.PrivateKey;
import java.security.PublicKey;
import org.example.age.data.DataMapper;
import org.example.age.data.DataStyle;
import org.example.age.data.crypto.DigitalSignature;
import org.immutables.value.Value;

/** Age certificate that is signed to verify the sender's identity. */
@Value.Immutable
@DataStyle
@JsonSerialize(as = ImmutableSignedAgeCertificate.class)
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
        } catch (JsonProcessingException e) {
            throw new RuntimeException("serialization failed", e);
        }
    }
}
