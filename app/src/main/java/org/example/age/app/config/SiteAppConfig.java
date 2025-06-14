package org.example.age.app.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.core.Configuration;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.example.age.module.client.SiteClientsConfig;
import org.example.age.module.crypto.demo.keys.SiteKeysConfig;
import org.example.age.module.store.dynamodb.client.DynamoDbConfig;
import org.example.age.module.store.redis.client.RedisConfig;
import org.example.age.service.SiteServiceConfig;

/** Configuration for the application. */
public final class SiteAppConfig extends Configuration {

    @Valid
    @NotNull
    private SiteServiceConfig service;

    @Valid
    @NotNull
    private SiteClientsConfig clients;

    @Valid
    @NotNull
    private RedisConfig redis;

    @Valid
    @NotNull
    private DynamoDbConfig dynamoDb;

    @Valid
    @NotNull
    private SiteKeysConfig keys;

    @JsonProperty
    public SiteServiceConfig getService() {
        return service;
    }

    @JsonProperty
    public void setService(SiteServiceConfig service) {
        this.service = service;
    }

    @JsonProperty
    public SiteClientsConfig getClients() {
        return clients;
    }

    @JsonProperty
    public void setClients(SiteClientsConfig clients) {
        this.clients = clients;
    }

    @JsonProperty
    public RedisConfig getRedis() {
        return redis;
    }

    @JsonProperty
    public void setRedis(RedisConfig redis) {
        this.redis = redis;
    }

    @JsonProperty
    public DynamoDbConfig getDynamoDb() {
        return dynamoDb;
    }

    @JsonProperty
    public void setDynamoDb(DynamoDbConfig dynamoDb) {
        this.dynamoDb = dynamoDb;
    }

    @JsonProperty
    public SiteKeysConfig getKeys() {
        return keys;
    }

    @JsonProperty
    public void setKeys(SiteKeysConfig keys) {
        this.keys = keys;
    }
}
