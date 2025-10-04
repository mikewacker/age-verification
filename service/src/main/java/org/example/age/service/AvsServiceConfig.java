package org.example.age.service;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.time.Duration;
import java.util.Map;
import org.example.age.avs.api.AvsApi;
import org.example.age.common.annotation.ValueStyle;
import org.example.age.common.api.AgeThresholds;
import org.immutables.value.Value;

/** Configuration for the service implementation of {@link AvsApi}. */
@Value.Immutable
@ValueStyle
@JsonSerialize
@JsonDeserialize(as = ImmutableAvsServiceConfig.class)
public interface AvsServiceConfig {

    /** Creates a builder for the configuration. */
    static Builder builder() {
        return new Builder();
    }

    /** Expiration for verification requests. */
    Duration verificationRequestExpiresIn();

    /** Age thresholds for each site, keyed by site ID. */
    Map<String, AgeThresholds> ageThresholds();

    /** Builder for the configuration. */
    final class Builder extends ImmutableAvsServiceConfig.Builder {

        Builder() {}
    }
}
