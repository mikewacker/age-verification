package org.example.age.module.store.dynamodb.client;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.annotation.Nullable;
import java.net.URI;
import org.example.age.common.annotation.ValueStyle;
import org.immutables.value.Value;
import software.amazon.awssdk.regions.Region;

/** Configuration for DynamoDB. */
@Value.Immutable
@ValueStyle
@JsonSerialize
@JsonDeserialize(as = ImmutableDynamoDbConfig.class)
public interface DynamoDbConfig {

    /** Creates a builder for the configuration. */
    static Builder builder() {
        return new Builder();
    }

    /** Gets the region. */
    String region();

    /** Gets the region. */
    @Value.Derived
    @JsonIgnore
    default Region regionAws() {
        return Region.of(region());
    }

    /** Override for the endpoint. Used for testing. */
    @Nullable
    URI testEndpointOverride();

    /** Builder for the configuration. */
    final class Builder extends ImmutableDynamoDbConfig.Builder {

        Builder() {}
    }
}
