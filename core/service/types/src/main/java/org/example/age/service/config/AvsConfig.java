package org.example.age.service.config;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.example.age.data.json.JsonStyle;
import org.immutables.value.Value;

/** Configuration for the age verification service. */
@Value.Immutable
@JsonStyle
@JsonDeserialize(as = ImmutableAvsConfig.class)
public interface AvsConfig {

    /** Creates a builder for the AVS configuration. */
    static Builder builder() {
        return new Builder();
    }

    /** Expiration (in seconds) for verification sessions. */
    long verificationSessionExpiresIn();

    /** Path to redirect the user to in order to continue age verification. */
    String redirectPath();

    final class Builder extends ImmutableAvsConfig.Builder {}
}
