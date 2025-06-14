package org.example.age.service;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.time.Duration;
import org.example.age.api.SiteApi;
import org.example.age.common.ValueStyle;
import org.immutables.value.Value;

/** Configuration for the service implementation of {@link SiteApi}. */
@Value.Immutable
@ValueStyle
@JsonSerialize
@JsonDeserialize(as = ImmutableSiteServiceConfig.class)
public interface SiteServiceConfig {

    /** Creates a builder for the configuration. */
    static Builder builder() {
        return new Builder();
    }

    /** ID of the site. */
    String id();

    /** Expiration for verified accounts. */
    Duration verifiedAccountExpiresIn();

    /** Builder for the configuration. */
    final class Builder extends ImmutableSiteServiceConfig.Builder {

        Builder() {}
    }
}
