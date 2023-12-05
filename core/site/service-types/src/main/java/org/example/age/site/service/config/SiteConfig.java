package org.example.age.site.service.config;

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

    /** Site ID that is registered with the age verification service. */
    String siteId();

    /** Expiration (in minutes) for verified accounts. */
    long expiresInMinutes();

    class Builder extends ImmutableSiteConfig.Builder {}
}