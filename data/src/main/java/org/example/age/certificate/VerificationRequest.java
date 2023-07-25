package org.example.age.certificate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.time.Duration;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import org.example.age.PackageImplementation;
import org.example.age.data.SecureId;
import org.immutables.value.Value;

/** Request to verify an account on a social media site. */
@Value.Immutable
@PackageImplementation
@JsonSerialize(as = ImmutableVerificationRequest.class)
@JsonDeserialize(as = ImmutableVerificationRequest.class)
public interface VerificationRequest {

    /** Creates a verification request. */
    static VerificationRequest of(SecureId id, String siteId, ZonedDateTime expiration) {
        return ImmutableVerificationRequest.builder()
                .id(id)
                .siteId(siteId)
                .expiration(expiration)
                .build();
    }

    /** Generates a verification request for the site. */
    static VerificationRequest generateForSite(String siteId, Duration expiresIn) {
        SecureId id = SecureId.generate();
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC).withNano(0);
        ZonedDateTime expiration = now.plus(expiresIn);
        return of(id, siteId, expiration);
    }

    /** ID of the request. */
    SecureId id();

    /** ID of the site. */
    String siteId();

    /** Time when the request expires. */
    ZonedDateTime expiration();

    /** Determines if the request is intended for a site. */
    default boolean isIntendedRecipient(String siteId) {
        return siteId.equals(siteId());
    }

    /** Determines if the request is expired. */
    @JsonIgnore
    default boolean isExpired() {
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        return expiration().isBefore(now);
    }
}
