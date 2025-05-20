package org.example.age.module.store.dynamodb.testing;

import java.net.URI;
import org.example.age.module.store.dynamodb.client.DynamoDbConfig;
import software.amazon.awssdk.regions.Region;

/** Configuration for testing. */
public final class TestConfig {

    /** Creates the configuration for DynamoDB. */
    public static DynamoDbConfig createDynamoDb(int port) {
        URI uri = URI.create(String.format("http://localhost:%d", port));
        return DynamoDbConfig.builder()
                .region(Region.US_EAST_1.toString())
                .testEndpointOverride(uri)
                .build();
    }

    // static class
    private TestConfig() {}
}
