package org.example.age.data.certificate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.github.mikewacker.drift.json.JsonStyle;
import java.time.Duration;
import org.example.age.data.crypto.SecureId;
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

    /**
     * Generates a verification request for a site.
     *
     * <p>The redirect URL will initially be a redirect path; it will later be converted to a redirect URL.
     * It is provided as a format string, so that the request ID can be incorporated into the redirect path.</p>
     */
    static VerificationRequest generateForSite(String siteId, Duration expiresIn, String redirectPathFormat) {
        SecureId id = SecureId.generate();
        long now = System.currentTimeMillis() / 1000;
        long expiration = now + expiresIn.toSeconds();
        String redirectPath = String.format(redirectPathFormat, id);
        return of(id, siteId, expiration, redirectPath);
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

    /** Converts the redirect path to a redirect URL. */
    default VerificationRequest convertRedirectPathToUrl(String rootUrl) {
        rootUrl = rootUrl.replaceFirst("/$", "");
        String redirectPath = redirectUrl().replaceFirst("^/", "");
        String redirectUrl = String.format("%s/%s", rootUrl, redirectPath);
        return VerificationRequest.of(id(), siteId(), expiration(), redirectUrl);
    }

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
