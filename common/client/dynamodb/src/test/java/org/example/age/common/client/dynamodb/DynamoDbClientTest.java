package org.example.age.common.client.dynamodb;

import dagger.BindsInstance;
import dagger.Component;
import jakarta.inject.Singleton;
import java.util.function.Supplier;
import org.example.age.testing.client.TestClient;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.BillingMode;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;

public final class DynamoDbClientTest {

    @Test
    public void useClient() {
        DynamoDbClient client = TestComponent.create();
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
    @Component(modules = DynamoDbClientModule.class)
    @Singleton
    interface TestComponent extends Supplier<DynamoDbClient> {

        static DynamoDbClient create() {
            DynamoDbClientConfig config = DynamoDbClientConfig.builder()
                    .region(Region.US_EAST_1.toString())
                    .testEndpointOverride(TestClient.localhostUri(8000))
                    .build();
            return DaggerDynamoDbClientTest_TestComponent.factory()
                    .create(config)
                    .get();
        }

        @Component.Factory
        interface Factory {

            TestComponent create(@BindsInstance DynamoDbClientConfig config);
        }
    }
}
