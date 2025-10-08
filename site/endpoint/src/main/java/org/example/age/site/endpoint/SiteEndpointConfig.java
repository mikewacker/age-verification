package org.example.age.site.endpoint;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.time.Duration;
import org.example.age.common.annotation.ValueStyle;
import org.example.age.site.api.SiteApi;
import org.immutables.value.Value;

/** Configuration for the {@link SiteApi} endpoint. */
@Value.Immutable
@ValueStyle
@JsonSerialize
@JsonDeserialize(as = ImmutableSiteEndpointConfig.class)
public interface SiteEndpointConfig {

    /** Creates a builder for the configuration. */
    static Builder builder() {
        return new Builder();
    }

    /** ID of the site. */
    String id();

    /** Expiration for verified accounts. */
    Duration verifiedAccountExpiresIn();

    /** Builder for the configuration. */
    final class Builder extends ImmutableSiteEndpointConfig.Builder {

        Builder() {}
    }
}
