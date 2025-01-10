package org.example.age.app.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.core.Configuration;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.example.age.module.client.AvsClientsConfig;
import org.example.age.module.crypto.demo.AvsKeysConfig;
import org.example.age.module.store.demo.AvsStoresConfig;
import org.example.age.service.AvsServiceConfig;

/** Configuration for the application. */
public final class AvsAppConfig extends Configuration {

    @Valid
    @NotNull
    private AvsServiceConfig service;

    @Valid
    @NotNull
    private AvsClientsConfig clients;

    @Valid
    @NotNull
    private AvsStoresConfig stores;

    @Valid
    @NotNull
    private AvsKeysConfig keys;

    @JsonProperty
    public AvsServiceConfig getService() {
        return service;
    }

    @JsonProperty
    public void setService(AvsServiceConfig service) {
        this.service = service;
    }

    @JsonProperty
    public AvsClientsConfig getClients() {
        return clients;
    }

    @JsonProperty
    public void setClients(AvsClientsConfig clients) {
        this.clients = clients;
    }

    @JsonProperty
    public AvsStoresConfig getStores() {
        return stores;
    }

    @JsonProperty
    public void setStores(AvsStoresConfig stores) {
        this.stores = stores;
    }

    @JsonProperty
    public AvsKeysConfig getKeys() {
        return keys;
    }

    @JsonProperty
    public void setKeys(AvsKeysConfig keys) {
        this.keys = keys;
    }
}
