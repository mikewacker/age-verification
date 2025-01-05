package org.example.age.module.client;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.net.URL;
import org.example.age.api.SiteApi;
import org.example.age.api.client.AvsApi;
import org.example.age.service.api.config.ConfigStyle;
import org.immutables.value.Value;

/** Configuration for the clients used by the service implementation of {@link SiteApi}. */
@Value.Immutable
@ConfigStyle
@JsonSerialize
@JsonDeserialize(as = ImmutableSiteClientsConfig.class)
public interface SiteClientsConfig {

    /** Creates a builder for the configuration. */
    static Builder builder() {
        return new Builder();
    }

    /** Base URL of the client for {@link AvsApi}. */
    URL avsUrl();

    /** Builder for the configuration. */
    final class Builder extends ImmutableSiteClientsConfig.Builder {

        Builder() {}
    }
}
