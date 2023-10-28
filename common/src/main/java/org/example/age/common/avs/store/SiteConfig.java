package org.example.age.common.avs.store;

import org.example.age.data.AgeThresholds;
import org.example.age.data.DataStyle;
import org.example.age.data.SecureId;
import org.immutables.value.Value;

/** Configuration for a registered site. */
@Value.Immutable
@DataStyle
public interface SiteConfig {

    /** Creates the configuration for a site. */
    static SiteConfig of(String siteId, AgeThresholds ageThresholds, SecureId pseudonymKey) {
        return ImmutableSiteConfig.builder()
                .siteId(siteId)
                .ageThresholds(ageThresholds)
                .pseudonymKey(pseudonymKey)
                .build();
    }

    /** ID of the site. */
    String siteId();

    /** Age thresholds that the site cares about. */
    AgeThresholds ageThresholds();

    /** Key used to localize pseudonyms for this site. */
    SecureId pseudonymKey();
}
