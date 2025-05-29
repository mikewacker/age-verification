package org.example.age.module.store.dynamodb.testing;

import org.example.age.common.testing.TestClient;
import org.example.age.module.store.dynamodb.client.DynamoDbConfig;
import org.example.age.testing.containers.TestContainers;
import software.amazon.awssdk.regions.Region;

/** Configuration for testing. */
public final class TestConfig {

    private static final DynamoDbConfig dynamoDb = DynamoDbConfig.builder()
            .region(Region.US_EAST_1.toString())
            .testEndpointOverride(TestClient.createLocalhostUri(TestContainers.DYNAMODB_PORT))
            .build();

    /** Gets the configuration for DynamoDB. */
    public static DynamoDbConfig dynamoDb() {
        return dynamoDb;
    }

    private TestConfig() {} // static class
}
