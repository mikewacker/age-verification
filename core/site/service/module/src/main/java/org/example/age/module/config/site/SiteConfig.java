package org.example.age.module.config.site;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.example.age.data.json.JsonStyle;
import org.example.age.module.config.common.AvsLocation;
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
