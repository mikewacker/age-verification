package org.example.age.data.certificate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.Duration;
import org.example.age.api.ApiStyle;
import org.example.age.data.crypto.SecureId;
import org.immutables.value.Value;

/** Request to verify an account on a social media site. */
@Value.Immutable
@ApiStyle
@JsonDeserialize(as = ImmutableVerificationRequest.class)
public interface VerificationRequest {

    /** Creates a verification request. */
    static VerificationRequest of(SecureId id, String siteId, long expiration) {
        return ImmutableVerificationRequest.builder()
                .id(id)
                .siteId(siteId)
                .expiration(expiration)
                .build();
    }

    /** Generates a verification request for the site. */
    static VerificationRequest generateForSite(String siteId, Duration expiresIn) {
        SecureId id = SecureId.generate();
        long now = System.currentTimeMillis() / 1000;
        long expiration = now + expiresIn.toSeconds();
        return of(id, siteId, expiration);
    }

    /** ID of the request. */
    SecureId id();

    /** ID of the site. */
    String siteId();

    /** Timestamp (in seconds) when the request expires. */
    long expiration();

    /** Determines if the request is intended for a site. */
    default boolean isIntendedRecipient(String siteId) {
        return siteId.equals(siteId());
    }

    /** Determines if the request is expired. */
    @JsonIgnore
    default boolean isExpired() {
        long now = System.currentTimeMillis() / 1000;
        return expiration() < now;
    }
}
