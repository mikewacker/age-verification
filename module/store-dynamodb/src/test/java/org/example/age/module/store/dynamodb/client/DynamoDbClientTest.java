package org.example.age.module.store.dynamodb.client;

import dagger.BindsInstance;
import dagger.Component;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import org.example.age.module.store.dynamodb.testing.TestDependenciesModule;
import org.example.age.testing.DynamoDbExtension;
import org.junit.jupiter.api.BeforeAll;
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

    @RegisterExtension
    private static final DynamoDbExtension dynamoDb = new DynamoDbExtension();

    private static DynamoDbClient client;

    @BeforeAll
    public static void createClient() {
        TestComponent component = TestComponent.create(dynamoDb.port());
        client = component.dynamoDbClient();
    }

    @Test
    public void useClient() throws Exception {
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

    /** Dagger component for the client. */
    @Component(modules = {DynamoDbClientModule.class, TestDependenciesModule.class})
    @Singleton
    interface TestComponent {

        static TestComponent create(int port) {
            return DaggerDynamoDbClientTest_TestComponent.factory().create(port);
        }

        DynamoDbClient dynamoDbClient();

        @Component.Factory
        interface Factory {

            TestComponent create(@BindsInstance @Named("port") int port);
        }
    }
}
