package org.example.age.module.client;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.net.URL;
import java.util.Map;
import org.example.age.api.AvsApi;
import org.example.age.api.client.SiteApi;
import org.example.age.service.api.config.ConfigStyle;
import org.immutables.value.Value;

/** Configuration for the clients used by the service implementation of {@link AvsApi}. */
@Value.Immutable
@ConfigStyle
@JsonSerialize
@JsonDeserialize(as = ImmutableAvsClientsConfig.class)
public interface AvsClientsConfig {

    /** Creates a builder for the configuration. */
    static Builder builder() {
        return new Builder();
    }

    /** Base URLs of the clients for {@link SiteApi}, keyed by site ID. */
    Map<String, URL> siteUrls();

    /** Builder for the configuration. */
    final class Builder extends ImmutableAvsClientsConfig.Builder {

        Builder() {}
    }
}
