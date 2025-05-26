package org.example.age.testing.containers;

import java.util.function.Consumer;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.BillingMode;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;

/** Test utilities for DynamoDB. */
public final class DynamoDbTestUtils {

    /** Creates the tables for the site account store. */
    public static void createSiteAccountStoreTables(DynamoDbClient client) {
        createTable(client, "Age.Verification.Account", builder -> builder.attributeDefinitions(
                        AttributeDefinition.builder()
                                .attributeName("AccountId")
                                .attributeType(ScalarAttributeType.S)
                                .build())
                .keySchema(KeySchemaElement.builder()
                        .attributeName("AccountId")
                        .keyType(KeyType.HASH)
                        .build()));
        createTable(client, "Age.Verification.Pseudonym", builder -> builder.attributeDefinitions(
                        AttributeDefinition.builder()
                                .attributeName("Pseudonym")
                                .attributeType(ScalarAttributeType.S)
                                .build())
                .keySchema(KeySchemaElement.builder()
                        .attributeName("Pseudonym")
                        .keyType(KeyType.HASH)
                        .build()));
    }

    /** Creates a table, waiting until it exists. */
    private static void createTable(
            DynamoDbClient client, String tableName, Consumer<CreateTableRequest.Builder> schemaSetter) {
        CreateTableRequest.Builder tableRequestBuilder =
                CreateTableRequest.builder().tableName(tableName).billingMode(BillingMode.PAY_PER_REQUEST);
        schemaSetter.accept(tableRequestBuilder);
        CreateTableRequest tableRequest = tableRequestBuilder.build();
        client.createTable(tableRequest);
        client.waiter().waitUntilTableExists(builder -> builder.tableName(tableName));
    }

    private DynamoDbTestUtils() {} // static class
}
