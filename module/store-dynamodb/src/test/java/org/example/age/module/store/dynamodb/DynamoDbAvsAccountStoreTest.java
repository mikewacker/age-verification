package org.example.age.module.store.dynamodb;

import dagger.Component;
import jakarta.inject.Singleton;
import org.example.age.api.testing.TestModels;
import org.example.age.module.store.dynamodb.testing.DynamoDbTestContainer;
import org.example.age.module.store.dynamodb.testing.TestDependenciesModule;
import org.example.age.service.module.store.AvsVerifiedUserStore;
import org.example.age.service.module.store.testing.AvsAccountStoreTestTemplate;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class DynamoDbAvsAccountStoreTest extends AvsAccountStoreTestTemplate {

    @RegisterExtension
    private static final DynamoDbTestContainer dynamoDb = new DynamoDbTestContainer();

    private static AvsVerifiedUserStore store;

    @BeforeAll
    public static void createAvsVerifiedUserStore() {
        TestComponent component = TestComponent.create();
        store = component.avsVerifiedUserStore();
    }

    @BeforeAll
    public static void setUpContainer() {
        dynamoDb.createAvsAccountStoreTables();
        dynamoDb.createAvsAccount("person", TestModels.createVerifiedUser());
    }

    @Override
    protected AvsVerifiedUserStore store() {
        return store;
    }

    /** Dagger component for the store. */
    @Component(modules = {DynamoDbAvsAccountStoreModule.class, TestDependenciesModule.class})
    @Singleton
    interface TestComponent {

        static TestComponent create() {
            return DaggerDynamoDbAvsAccountStoreTest_TestComponent.create();
        }

        AvsVerifiedUserStore avsVerifiedUserStore();
    }
}
