package org.example.age.common.avs.config;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.example.age.common.service.data.SiteLocation;
import org.example.age.data.crypto.SecureId;
import org.example.age.data.user.AgeThresholds;
import org.example.age.data.utils.DataStyle;
import org.immutables.value.Value;

/** Configuration for a registered site. */
@Value.Immutable
@DataStyle
@JsonSerialize(as = ImmutableRegisteredSiteConfig.class)
@JsonDeserialize(as = ImmutableRegisteredSiteConfig.class)
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
