package org.example.age.data.certificate;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.example.age.api.ApiStyle;
import org.example.age.data.crypto.AesGcmEncryptionPackage;
import org.example.age.data.user.VerifiedUser;
import org.immutables.value.Value;

/** Certificate that pseudonymously verifies the age (and guardians, if applicable) of a person. */
@Value.Immutable
@ApiStyle
@JsonDeserialize(as = ImmutableAgeCertificate.class)
public interface AgeCertificate {

    /** Creates an unsigned age certificate to fulfill a verification request for a verified user. */
    static AgeCertificate of(VerificationRequest request, VerifiedUser user, AesGcmEncryptionPackage authToken) {
        return ImmutableAgeCertificate.builder()
                .verificationRequest(request)
                .verifiedUser(user)
                .authToken(authToken)
                .build();
    }

    /** Verification request to fulfill. */
    VerificationRequest verificationRequest();

    /** Verified user. */
    VerifiedUser verifiedUser();

    /** Encrypted authentication data. */
    AesGcmEncryptionPackage authToken();
}
