package org.example.age.avs.app.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.core.Configuration;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.example.age.avs.api.AvsApi;
import org.example.age.avs.endpoint.AvsEndpointConfig;

/** Configuration for the application. */
public final class AvsAppConfig extends Configuration {

    @Valid
    @NotNull
    private AvsEndpointConfig endpoint;

    @Valid
    @NotNull
    private AvsClientsConfig clients;

    @Valid
    @NotNull
    private AvsKeysConfig keys;

    /** Gets the configuration for the {@link AvsApi} endpoint. */
    @JsonProperty
    public AvsEndpointConfig getEndpoint() {
        return endpoint;
    }

    /** Sets the configuration for the {@link AvsApi} endpoint. */
    @JsonProperty
    public void setEndpoint(AvsEndpointConfig endpoint) {
        this.endpoint = endpoint;
    }

    /** Gets the configuration for the clients. */
    @JsonProperty
    public AvsClientsConfig getClients() {
        return clients;
    }

    /** Sets the configuration for the clients. */
    @JsonProperty
    public void setClients(AvsClientsConfig clients) {
        this.clients = clients;
    }

    /** Gets the configuration for the keys. */
    @JsonProperty
    public AvsKeysConfig getKeys() {
        return keys;
    }

    /** Sets the configuration for the keys. */
    @JsonProperty
    public void setKeys(AvsKeysConfig keys) {
        this.keys = keys;
    }
}
