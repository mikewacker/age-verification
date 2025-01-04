package org.example.age.service;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.time.Duration;
import org.example.age.api.AvsApi;
import org.immutables.value.Value;

/** Configuration for the service implementation of {@link AvsApi}. */
@Value.Immutable
@ConfigStyle
@JsonSerialize
@JsonDeserialize(as = ImmutableAvsServiceConfig.class)
public interface AvsServiceConfig {

    /** Creates a builder for the configuration. */
    static Builder builder() {
        return new Builder();
    }

    /** Expiration for verification requests. */
    Duration verificationRequestExpiresIn();

    /** Builder for the configuration. */
    final class Builder extends ImmutableAvsServiceConfig.Builder {

        Builder() {}
    }
}
