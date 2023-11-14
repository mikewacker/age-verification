package org.example.age.common.avs.config;

import org.example.age.data.AgeThresholds;
import org.example.age.data.DataStyle;
import org.example.age.data.crypto.SecureId;
import org.immutables.value.Value;

/** Configuration for a registered site. */
@Value.Immutable
@DataStyle
public interface RegisteredSiteConfig {

    /** Creates a builder for the site configuration. */
    static RegisteredSiteConfig.Builder builder(String siteId) {
        return new Builder().siteId(siteId);
    }

    /** ID of the site. */
    String siteId();

    /** URL location of the site. */
    SiteLocation siteLocation();

    /** Age thresholds that the site cares about. */
    AgeThresholds ageThresholds();

    /** Key used to localize pseudonyms for this site. */
    SecureId pseudonymKey();

    final class Builder extends ImmutableRegisteredSiteConfig.Builder {}
}
