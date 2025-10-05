package org.example.age.avs.app.config;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.example.age.avs.client.site.SiteClientsConfig;
import org.example.age.common.annotation.ValueStyle;
import org.example.age.common.client.dynamodb.DynamoDbClientConfig;
import org.example.age.common.client.redis.RedisClientConfig;
import org.immutables.value.Value;

/** Configuration for the clients. */
@Value.Immutable
@ValueStyle
@JsonSerialize
@JsonDeserialize(as = ImmutableAvsClientsConfig.class)
public interface AvsClientsConfig {

    /** Creates a builder for the configuration. */
    static Builder builder() {
        return new Builder();
    }

    /** Configuration for the site clients. */
    SiteClientsConfig sites();

    /** Configuration for the DynamoDB client. */
    DynamoDbClientConfig dynamoDb();

    /** Configuration for the Redis client. */
    RedisClientConfig redis();

    /** Builder for the configuration. */
    final class Builder extends ImmutableAvsClientsConfig.Builder {

        Builder() {}
    }
}
