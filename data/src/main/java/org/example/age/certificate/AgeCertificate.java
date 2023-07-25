package org.example.age.certificate;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.security.PrivateKey;
import java.security.PublicKey;
import org.example.age.PackageImplementation;
import org.example.age.data.VerifiedUser;
import org.immutables.value.Value;

/**
 * Certificate that verifies the age and guardians (if applicable) of a user.
 *
 * <p>Only Ed25519 keys are supported.</p>
 */
@Value.Immutable
@PackageImplementation
@JsonSerialize(as = ImmutableAgeCertificate.class)
@JsonDeserialize(as = ImmutableAgeCertificate.class)
public interface AgeCertificate {

    /** Creates an unsigned age certificate to fulfill a verification request for a verified user. */
    static AgeCertificate of(VerificationRequest request, VerifiedUser user) {
        return ImmutableAgeCertificate.builder()
                .verificationRequest(request)
                .verifiedUser(user)
                .build();
    }

    /**
     * Verifies a signed age certificate's signature, recipient, and expiration.
     * Throws an {@link IllegalArgumentException} if verification fails.
     */
    static AgeCertificate verifyForSite(byte[] signedCertificate, PublicKey publicKey, String siteId) {
        int certificateLength = SignatureUtils.verify(signedCertificate, publicKey);
        AgeCertificate certificate =
                SerializationUtils.deserialize(signedCertificate, SignatureUtils.MESSAGE_OFFSET, certificateLength);
        if (!certificate.verificationRequest().isIntendedRecipient(siteId)) {
            throw new IllegalArgumentException("wrong recipient");
        }

        if (certificate.verificationRequest().isExpired()) {
            throw new IllegalArgumentException("expired age certificate");
        }

        return certificate;
    }

    /** Verification request to fulfill. */
    VerificationRequest verificationRequest();

    /** Verified user. */
    VerifiedUser verifiedUser();

    /** Signs the certificate. */
    default byte[] sign(PrivateKey privateKey) {
        byte[] rawCertificate = SerializationUtils.serialize(this);
        return SignatureUtils.sign(rawCertificate, privateKey);
    }
}
