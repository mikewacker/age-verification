package org.example.age.site.service.config;

import java.time.Duration;
import org.example.age.common.service.config.AvsLocation;
import org.example.age.data.utils.DataStyle;
import org.immutables.value.Value;

/** Configuration for a site. */
@Value.Immutable
@DataStyle
public interface SiteConfig {

    /** Creates a builder for the site configuration. */
    static Builder builder() {
        return new Builder();
    }

    /** URL location of the age verification service. */
    AvsLocation avsLocation();

    /** Site ID that is expected for age certificates. */
    String siteId();

    /** Expiration for verified accounts. */
    Duration expiresIn();

    class Builder extends ImmutableSiteConfig.Builder {}
}
