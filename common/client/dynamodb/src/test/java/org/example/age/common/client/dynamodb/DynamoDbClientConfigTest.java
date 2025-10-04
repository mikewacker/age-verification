package org.example.age.common.client.dynamodb;

import java.io.IOException;
import org.example.age.testing.client.TestClient;
import org.example.age.testing.json.JsonTesting;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.regions.Region;

public final class DynamoDbClientConfigTest {

    @Test
    public void serializeThenDeserialize_Prod() throws IOException {
        DynamoDbClientConfig config = DynamoDbClientConfig.builder()
                .region(Region.US_EAST_1.toString())
                .build();
        JsonTesting.serializeThenDeserialize(config, DynamoDbClientConfig.class);
    }

    @Test
    public void serializeThenDeserialize_Test() throws IOException {
        DynamoDbClientConfig config = DynamoDbClientConfig.builder()
                .region(Region.US_EAST_1.toString())
                .testEndpointOverride(TestClient.localhostUri(8000))
                .build();
        JsonTesting.serializeThenDeserialize(config, DynamoDbClientConfig.class);
    }
}
