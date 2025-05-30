package org.example.age.module.store.dynamodb.testing;

import java.util.List;
import java.util.function.Consumer;
import org.example.age.common.testing.TestClient;
import org.example.age.module.common.testing.BaseTestContainer;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.BillingMode;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;

public final class DynamoDbTestContainer extends BaseTestContainer<DynamoDbClient> {

    public static final int PORT = 8000;

    /** Creates the tables for the site account store. */
    public void createSiteAccountStoreTables() {
        createTable("Age.Verification.Account", builder -> builder.attributeDefinitions(AttributeDefinition.builder()
                        .attributeName("AccountId")
                        .attributeType(ScalarAttributeType.S)
                        .build())
                .keySchema(KeySchemaElement.builder()
                        .attributeName("AccountId")
                        .keyType(KeyType.HASH)
                        .build()));
        createTable("Age.Verification.Pseudonym", builder -> builder.attributeDefinitions(AttributeDefinition.builder()
                        .attributeName("Pseudonym")
                        .attributeType(ScalarAttributeType.S)
                        .build())
                .keySchema(KeySchemaElement.builder()
                        .attributeName("Pseudonym")
                        .keyType(KeyType.HASH)
                        .build()));
    }

    @Override
    protected DynamoDbClient createClient() {
        return DynamoDbClient.builder()
                .region(Region.US_EAST_1)
                .endpointOverride(TestClient.createLocalhostUri(PORT))
                .credentialsProvider(
                        StaticCredentialsProvider.create(AwsBasicCredentials.create("dummyKey", "dummySecret")))
                .build();
    }

    @Override
    protected void clean(DynamoDbClient client) {
        List<String> tableNames = client.listTables().tableNames();
        for (String tableName : tableNames) {
            client.deleteTable(builder -> builder.tableName(tableName));
        }
        for (String tableName : tableNames) {
            client.waiter().waitUntilTableNotExists(builder -> builder.tableName(tableName));
        }
    }

    @Override
    protected void closeClient(DynamoDbClient client) {
        client.close();
    }

    /** Creates a table, waiting until it exists. */
    private void createTable(String tableName, Consumer<CreateTableRequest.Builder> schemaSetter) {
        CreateTableRequest.Builder tableRequestBuilder =
                CreateTableRequest.builder().tableName(tableName).billingMode(BillingMode.PAY_PER_REQUEST);
        schemaSetter.accept(tableRequestBuilder);
        CreateTableRequest tableRequest = tableRequestBuilder.build();
        getClient().createTable(tableRequest);
        getClient().waiter().waitUntilTableExists(builder -> builder.tableName(tableName));
    }
}
