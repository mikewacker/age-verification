package org.example.age.testing.containers;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import redis.clients.jedis.JedisPooled;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.BillingMode;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;

public final class TestContainersTest {

    @RegisterExtension
    private static final TestContainers containers = new TestContainers();

    @Test
    public void redis() {
        JedisPooled client = containers.redisClient();
        client.set("key", "value");
        String value = client.get("key");
        assertThat(value).isEqualTo("value");
    }

    @Test
    public void dynamoDb() {
        DynamoDbClient client = containers.dynamoDbClient();
        CreateTableRequest tableRequest = CreateTableRequest.builder()
                .tableName("table")
                .attributeDefinitions(AttributeDefinition.builder()
                        .attributeName("id")
                        .attributeType(ScalarAttributeType.S)
                        .build())
                .keySchema(KeySchemaElement.builder()
                        .attributeName("id")
                        .keyType(KeyType.HASH)
                        .build())
                .billingMode(BillingMode.PAY_PER_REQUEST) // required for DynamoDB local
                .build();
        client.createTable(tableRequest);
        client.waiter().waitUntilTableExists(builder -> builder.tableName("table"));

        AttributeValue idS = AttributeValue.fromS("abc123");
        AttributeValue dataS = AttributeValue.fromS("Hello, world!");
        PutItemRequest putRequest = PutItemRequest.builder()
                .tableName("table")
                .item(Map.of("id", idS, "data", dataS))
                .build();
        client.putItem(putRequest);

        GetItemRequest getRequest = GetItemRequest.builder()
                .tableName("table")
                .key(Map.of("id", idS))
                .attributesToGet("data")
                .build();
        AttributeValue storedDataS = client.getItem(getRequest).item().get("data");
        assertThat(storedDataS).isEqualTo(dataS);
    }
}
