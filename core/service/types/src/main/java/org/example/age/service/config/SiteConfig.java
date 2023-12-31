package org.example.age.service.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.github.mikewacker.drift.json.JsonStyle;
import org.immutables.value.Value;

/** Configuration for a site. */
@Value.Immutable
@JsonStyle
@JsonDeserialize(as = ImmutableSiteConfig.class)
public interface SiteConfig {

    /** Creates a builder for the site configuration. */
    static Builder builder() {
        return new Builder();
    }

    /** ID that is registered with the age verification service. */
    String id();

    /** Expiration (in minutes) for verified accounts. */
    long verifiedAccountExpiresInMinutes();

    /** Expiration (in seconds) for verified accounts. */
    @Value.Derived
    @JsonIgnore
    default long verifiedAccountExpiresIn() {
        return verifiedAccountExpiresInMinutes() * 60;
    }

    /** Path to redirect the user to in order to continue age verification. */
    String redirectPath();

    class Builder extends ImmutableSiteConfig.Builder {}
}
