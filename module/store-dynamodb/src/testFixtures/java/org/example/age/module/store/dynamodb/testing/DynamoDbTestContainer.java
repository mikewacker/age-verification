package org.example.age.module.store.dynamodb.testing;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import org.example.age.common.api.VerifiedUser;
import org.example.age.module.common.testing.BaseTestContainer;
import org.example.age.testing.util.TestClient;
import org.example.age.testing.util.TestObjectMapper;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.BillingMode;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;

/** Test container for DynamoDB that has been cleaned. */
public final class DynamoDbTestContainer extends BaseTestContainer<DynamoDbClient> {

    public static final int PORT = 8000;

    /** Creates the tables for the account store on the site. */
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

    /** Creates the tables for the account store on the age verification service. */
    public void createAvsAccountStoreTables() {
        createTable("Age.User", builder -> builder.attributeDefinitions(AttributeDefinition.builder()
                        .attributeName("AccountId")
                        .attributeType(ScalarAttributeType.S)
                        .build())
                .keySchema(KeySchemaElement.builder()
                        .attributeName("AccountId")
                        .keyType(KeyType.HASH)
                        .build()));
    }

    /** Creates an account on the age verification service. */
    public void createAvsAccount(String accountId, VerifiedUser user) {
        AttributeValue accountIdS = AttributeValue.fromS(accountId);
        AttributeValue userS = AttributeValue.fromS(TestObjectMapper.serialize(user));
        PutItemRequest userRequest = PutItemRequest.builder()
                .tableName("Age.User")
                .item(Map.of("AccountId", accountIdS, "User", userS))
                .build();
        getClient().putItem(userRequest);
    }

    @Override
    protected DynamoDbClient createClient() {
        return DynamoDbClient.builder()
                .region(Region.US_EAST_1)
                .endpointOverride(TestClient.localhostUri(PORT))
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
