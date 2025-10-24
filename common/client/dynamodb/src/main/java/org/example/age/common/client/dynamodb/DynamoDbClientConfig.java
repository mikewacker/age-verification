package org.example.age.common.client.dynamodb;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.annotation.Nullable;
import java.net.URI;
import org.example.age.common.annotation.ValueStyle;
import org.immutables.value.Value;
import software.amazon.awssdk.regions.Region;

/** Configuration for the DynamoDB client. */
@Value.Immutable
@ValueStyle
@JsonDeserialize(as = ImmutableDynamoDbClientConfig.class)
public interface DynamoDbClientConfig {

    /** Creates a builder for the configuration. */
    static Builder builder() {
        return new Builder();
    }

    /** Gets the region. */
    String region();

    /** Gets the region. */
    @Value.Derived
    default Region regionAws() {
        return Region.of(region());
    }

    /** Override for the endpoint. Used for testing. */
    @Nullable
    URI testEndpointOverride();

    /** Builder for the configuration. */
    final class Builder extends ImmutableDynamoDbClientConfig.Builder {

        Builder() {}
    }
}
