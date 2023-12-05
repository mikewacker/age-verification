package org.example.age.site.service.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.example.age.api.ApiStyle;
import org.example.age.common.service.config.AvsLocation;
import org.immutables.value.Value;

/** Configuration for a site. */
@Value.Immutable
@ApiStyle
@JsonDeserialize(as = ImmutableSiteConfig.class)
public interface SiteConfig {

    /** Creates a builder for the site configuration. */
    static Builder builder() {
        return new Builder();
    }

    /** URL location of the age verification service. */
    AvsLocation avsLocation();

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

    class Builder extends ImmutableSiteConfig.Builder {}
}
