package org.example.age.module.store.dynamodb.testing;

import java.net.URI;
import org.example.age.module.store.dynamodb.client.DynamoDbConfig;
import org.example.age.testing.containers.TestContainers;
import software.amazon.awssdk.regions.Region;

/** Configuration for testing. */
public final class TestConfig {

    private static final DynamoDbConfig dynamoDb = DynamoDbConfig.builder()
            .region(Region.US_EAST_1.toString())
            .testEndpointOverride(URI.create(String.format("http://localhost:%d", TestContainers.DYNAMODB_PORT)))
            .build();

    /** Gets the configuration for DynamoDB. */
    public static DynamoDbConfig dynamoDb() {
        return dynamoDb;
    }

    private TestConfig() {} // static class
}
