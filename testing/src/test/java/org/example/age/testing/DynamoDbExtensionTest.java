package org.example.age.testing;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.BillingMode;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;

public final class DynamoDbExtensionTest {

    @RegisterExtension
    private static final DynamoDbExtension dynamoDb = new DynamoDbExtension();

    @Test
    public void useDynamoDb() {
        // Create table.
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
                .billingMode(BillingMode.PAY_PER_REQUEST)
                .build();
        dynamoDb.client().createTable(tableRequest);
        dynamoDb.client().waiter().waitUntilTableExists(builder -> builder.tableName("table"));

        // Put item in table.
        PutItemRequest putRequest = PutItemRequest.builder()
                .tableName("table")
                .item(Map.of(
                        "id", AttributeValue.fromS("abc123"),
                        "data", AttributeValue.fromS("Hello, world!")))
                .build();
        dynamoDb.client().putItem(putRequest);

        // Get item from table.
        GetItemRequest getRequest = GetItemRequest.builder()
                .tableName("table")
                .key(Map.of("id", AttributeValue.fromS("abc123")))
                .attributesToGet("data")
                .build();
        AttributeValue data = dynamoDb.client().getItem(getRequest).item().get("data");
        assertThat(data).isNotNull().extracting(AttributeValue::s).isEqualTo("Hello, world!");
    }
}
