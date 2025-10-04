package org.example.age.site.app.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.core.Configuration;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.example.age.common.client.dynamodb.DynamoDbClientConfig;
import org.example.age.common.client.redis.RedisClientConfig;
import org.example.age.module.client.SiteClientsConfig;
import org.example.age.module.crypto.demo.keys.SiteKeysConfig;
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
    private RedisClientConfig redis;

    @Valid
    @NotNull
    private DynamoDbClientConfig dynamoDb;

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
    public RedisClientConfig getRedis() {
        return redis;
    }

    @JsonProperty
    public void setRedis(RedisClientConfig redis) {
        this.redis = redis;
    }

    @JsonProperty
    public DynamoDbClientConfig getDynamoDb() {
        return dynamoDb;
    }

    @JsonProperty
    public void setDynamoDb(DynamoDbClientConfig dynamoDb) {
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
