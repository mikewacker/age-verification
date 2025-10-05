package org.example.age.avs.client.site;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.net.URL;
import java.util.Map;
import org.example.age.common.annotation.ValueStyle;
import org.immutables.value.Value;

/** Configuration for the site clients. */
@Value.Immutable
@ValueStyle
@JsonSerialize
@JsonDeserialize(as = ImmutableSiteClientsConfig.class)
public interface SiteClientsConfig {

    /** Creates a builder for the configuration. */
    static Builder builder() {
        return new Builder();
    }

    /** URLs of the sites, keyed by site ID. */
    Map<String, URL> urls();

    /** Builder for the configuration. */
    final class Builder extends ImmutableSiteClientsConfig.Builder {

        Builder() {}
    }
}
