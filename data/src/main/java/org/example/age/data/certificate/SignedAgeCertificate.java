package org.example.age.data.certificate;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.example.age.data.DataStyle;
import org.immutables.value.Value;

/** Age certificate that is signed to verify the sender's identity. */
@Value.Immutable
@DataStyle
@JsonSerialize(as = ImmutableSignedAgeCertificate.class)
@JsonDeserialize(as = ImmutableSignedAgeCertificate.class)
public interface SignedAgeCertificate {

    static SignedAgeCertificate of(AgeCertificate certificate, DigitalSignature signature) {
        return ImmutableSignedAgeCertificate.builder()
                .ageCertificate(certificate)
                .signature(signature)
                .build();
    }

    /** Age certificate. */
    AgeCertificate ageCertificate();

    /** Signature for the age certificate. */
    DigitalSignature signature();
}
