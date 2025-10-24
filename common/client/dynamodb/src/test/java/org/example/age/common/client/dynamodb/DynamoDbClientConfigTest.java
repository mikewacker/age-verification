package org.example.age.common.client.dynamodb;

import org.example.age.testing.config.TestConfigParser;
import org.junit.jupiter.api.Test;

public final class DynamoDbClientConfigTest {

    @Test
    public void parse_Prod() throws Exception {
        TestConfigParser.forClass(DynamoDbClientConfig.class).parseLines("region: us-east-1");
    }

    @Test
    public void parse_Test() throws Exception {
        TestConfigParser.forClass(DynamoDbClientConfig.class)
                .parseLines("region: us-east-1", "testEndpointOverride: http://localhost:8000");
    }
}
