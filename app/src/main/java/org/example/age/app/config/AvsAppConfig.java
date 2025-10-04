package org.example.age.app.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.core.Configuration;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.example.age.common.client.dynamodb.DynamoDbClientConfig;
import org.example.age.common.client.redis.RedisClientConfig;
import org.example.age.module.client.AvsClientsConfig;
import org.example.age.module.crypto.demo.keys.AvsKeysConfig;
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
    private RedisClientConfig redis;

    @Valid
    @NotNull
    private DynamoDbClientConfig dynamoDb;

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
    public AvsKeysConfig getKeys() {
        return keys;
    }

    @JsonProperty
    public void setKeys(AvsKeysConfig keys) {
        this.keys = keys;
    }
}
