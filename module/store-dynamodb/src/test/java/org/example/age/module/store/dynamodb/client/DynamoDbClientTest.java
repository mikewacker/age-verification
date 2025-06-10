package org.example.age.module.store.dynamodb.client;

import dagger.Component;
import jakarta.inject.Singleton;
import java.util.function.Supplier;
import org.example.age.module.store.dynamodb.testing.DynamoDbTestContainer;
import org.example.age.module.store.dynamodb.testing.TestDependenciesModule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.BillingMode;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;

public final class DynamoDbClientTest {

    private static final DynamoDbClient client = TestComponent.create();

    @RegisterExtension
    private static final DynamoDbTestContainer dynamoDb = new DynamoDbTestContainer();

    @Test
    public void useClient() {
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
        client.createTable(tableRequest);
        client.waiter().waitUntilTableExists(builder -> builder.tableName("table"));
    }

    /** Dagger component for {@link DynamoDbClient} */
    @Component(modules = {DynamoDbClientModule.class, TestDependenciesModule.class})
    @Singleton
    interface TestComponent extends Supplier<DynamoDbClient> {

        static DynamoDbClient create() {
            return DaggerDynamoDbClientTest_TestComponent.create().get();
        }
    }
}
