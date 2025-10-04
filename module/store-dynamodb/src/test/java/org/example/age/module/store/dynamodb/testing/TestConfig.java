package org.example.age.module.store.dynamodb.testing;

import org.example.age.common.client.dynamodb.DynamoDbClientConfig;
import org.example.age.testing.client.TestClient;
import software.amazon.awssdk.regions.Region;

/** Configuration for testing. */
public final class TestConfig {

    private static final DynamoDbClientConfig dynamoDb = DynamoDbClientConfig.builder()
            .region(Region.US_EAST_1.toString())
            .testEndpointOverride(TestClient.localhostUri(DynamoDbTestContainer.PORT))
            .build();

    /** Gets the configuration for DynamoDB. */
    public static DynamoDbClientConfig dynamoDb() {
        return dynamoDb;
    }

    private TestConfig() {} // static class
}
