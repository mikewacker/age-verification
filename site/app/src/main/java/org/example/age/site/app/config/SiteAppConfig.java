package org.example.age.site.app.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.core.Configuration;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.example.age.site.api.SiteApi;
import org.example.age.site.endpoint.SiteEndpointConfig;

/** Configuration for the application. */
public final class SiteAppConfig extends Configuration {

    @Valid
    @NotNull
    private SiteEndpointConfig endpoint;

    @Valid
    @NotNull
    private SiteClientsConfig clients;

    @Valid
    @NotNull
    private SiteKeysConfig keys;

    /** Gets the configuration for the {@link SiteApi} endpoint. */
    @JsonProperty
    public SiteEndpointConfig getEndpoint() {
        return endpoint;
    }

    /** Sets the configuration for the {@link SiteApi} endpoint. */
    @JsonProperty
    public void setEndpoint(SiteEndpointConfig endpoint) {
        this.endpoint = endpoint;
    }

    /** Gets the configuration for the clients. */
    @JsonProperty
    public SiteClientsConfig getClients() {
        return clients;
    }

    /** Sets the configuration for the clients. */
    @JsonProperty
    public void setClients(SiteClientsConfig clients) {
        this.clients = clients;
    }

    /** Gets the configuration for the keys. */
    @JsonProperty
    public SiteKeysConfig getKeys() {
        return keys;
    }

    /** Sets the configuration for the keys. */
    @JsonProperty
    public void setKeys(SiteKeysConfig keys) {
        this.keys = keys;
    }
}
