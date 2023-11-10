package org.example.age.data.certificate;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.security.PrivateKey;
import java.security.PublicKey;
import org.example.age.data.DataStyle;
import org.example.age.data.VerifiedUser;
import org.example.age.data.internal.SerializationUtils;
import org.immutables.value.Value;

/**
 * Certificate that pseudonymously verifies the age (and guardians, if applicable) of a person.
 *
 * <p>Only Ed25519 keys are supported.</p>
 */
@Value.Immutable
@DataStyle
@JsonSerialize(as = ImmutableAgeCertificate.class)
@JsonDeserialize(as = ImmutableAgeCertificate.class)
public interface AgeCertificate {

    /** Creates an unsigned age certificate to fulfill a verification request for a verified user. */
    static AgeCertificate of(VerificationRequest request, VerifiedUser user, AuthToken token) {
        return ImmutableAgeCertificate.builder()
                .verificationRequest(request)
                .verifiedUser(user)
                .authToken(token)
                .build();
    }

    /**
     * Verifies a signed age certificate's signature, recipient, and expiration.
     * Throws an {@link IllegalArgumentException} if verification fails.
     */
    static AgeCertificate verifyForSite(byte[] signedCertificate, PublicKey publicKey, String siteId) {
        int certificateLength = SignatureUtils.verifyLegacy(signedCertificate, publicKey);
        AgeCertificate certificate = SerializationUtils.deserialize(
                signedCertificate, SignatureUtils.MESSAGE_OFFSET, certificateLength, AgeCertificate.class);
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

    /** Authentication data, which is encrypted using an ephemeral key. */
    AuthToken authToken();

    /** Signs the certificate. */
    default byte[] sign(PrivateKey privateKey) {
        byte[] rawCertificate = SerializationUtils.serialize(this);
        return SignatureUtils.signLegacy(rawCertificate, privateKey);
    }
}
