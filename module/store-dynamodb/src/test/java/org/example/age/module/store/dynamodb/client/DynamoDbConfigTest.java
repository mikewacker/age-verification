package org.example.age.module.store.dynamodb.client;

import java.io.IOException;
import org.example.age.module.store.dynamodb.testing.TestConfig;
import org.example.age.testing.json.JsonTesting;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.regions.Region;

public final class DynamoDbConfigTest {

    @Test
    public void serializeThenDeserialize() throws IOException {
        JsonTesting.serializeThenDeserialize(TestConfig.dynamoDb(), DynamoDbConfig.class);
    }

    @Test
    public void serializeThenDeserialize_Prod() throws IOException {
        DynamoDbConfig config =
                DynamoDbConfig.builder().region(Region.US_EAST_1.toString()).build();
        JsonTesting.serializeThenDeserialize(config, DynamoDbConfig.class);
    }
}
