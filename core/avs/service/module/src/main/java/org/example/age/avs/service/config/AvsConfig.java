package org.example.age.avs.service.config;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.example.age.api.ApiStyle;
import org.immutables.value.Value;

/** Configuration for the age verification service. */
@Value.Immutable
@ApiStyle
@JsonDeserialize(as = ImmutableAvsConfig.class)
public interface AvsConfig {

    /** Creates a builder for the AVS configuration. */
    static Builder builder() {
        return new Builder();
    }

    /** Expiration (in seconds) for verification sessions. */
    long verificationSessionExpiresIn();

    final class Builder extends ImmutableAvsConfig.Builder {}
}
