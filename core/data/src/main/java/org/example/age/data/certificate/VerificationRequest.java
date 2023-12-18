package org.example.age.data.certificate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.Duration;
import org.example.age.data.crypto.SecureId;
import org.example.age.data.json.JsonStyle;
import org.immutables.value.Value;

/** Request to verify an account on a site. */
@Value.Immutable
@JsonStyle
@JsonDeserialize(as = ImmutableVerificationRequest.class)
public interface VerificationRequest {

    /** Creates a verification request. */
    static VerificationRequest of(SecureId id, String siteId, long expiration, String redirectUrl) {
        return ImmutableVerificationRequest.builder()
                .id(id)
                .siteId(siteId)
                .expiration(expiration)
                .redirectUrl(redirectUrl)
                .build();
    }

    /** Generates a verification request for the site. */
    static VerificationRequest generateForSite(String siteId, Duration expiresIn, String redirectUrl) {
        SecureId id = SecureId.generate();
        long now = System.currentTimeMillis() / 1000;
        long expiration = now + expiresIn.toSeconds();
        return of(id, siteId, expiration, redirectUrl);
    }

    /** ID of the request. */
    SecureId id();

    /**
     * ID of the site.
     *
     * <p>Also prevents surreptitious forwarding.</p>
     */
    String siteId();

    /** Timestamp (in seconds) when the request expires. */
    long expiration();

    /** URL to redirect the user to in order to continue age verification. */
    String redirectUrl();

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
