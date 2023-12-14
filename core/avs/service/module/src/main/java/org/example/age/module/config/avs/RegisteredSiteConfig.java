package org.example.age.module.config.avs;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.example.age.data.json.JsonStyle;
import org.example.age.data.user.AgeThresholds;
import org.example.age.module.location.common.SiteLocation;
import org.immutables.value.Value;

/** Configuration for a site that is registered with the age verification service. */
@Value.Immutable
@JsonStyle
@JsonDeserialize(as = ImmutableRegisteredSiteConfig.class)
public interface RegisteredSiteConfig {

    /** Creates a builder for the registered site configuration. */
    static Builder builder(String id) {
        return new Builder().id(id);
    }

    /** Registered ID for the site. */
    String id();

    /** URL location of the site. */
    SiteLocation location();

    /** Age thresholds that the site cares about. */
    AgeThresholds ageThresholds();

    final class Builder extends ImmutableRegisteredSiteConfig.Builder {}
}
