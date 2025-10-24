package org.example.age.site.app.config;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.example.age.common.annotation.ValueStyle;
import org.example.age.common.client.dynamodb.DynamoDbClientConfig;
import org.example.age.common.client.redis.RedisClientConfig;
import org.example.age.site.client.avs.AvsClientConfig;
import org.immutables.value.Value;

/** Configuration for the clients. */
@Value.Immutable
@ValueStyle
@JsonDeserialize(as = ImmutableSiteClientsConfig.class)
public interface SiteClientsConfig {

    /** Creates a builder for the configuration. */
    static Builder builder() {
        return new Builder();
    }

    /** Configuration for the age verification service client. */
    AvsClientConfig avs();

    /** Configuration for the DynamoDB client. */
    DynamoDbClientConfig dynamoDb();

    /** Configuration for the Redis client. */
    RedisClientConfig redis();

    /** Builder for the configuration. */
    final class Builder extends ImmutableSiteClientsConfig.Builder {

        Builder() {}
    }
}
