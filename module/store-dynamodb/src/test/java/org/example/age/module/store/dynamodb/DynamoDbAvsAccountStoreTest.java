package org.example.age.module.store.dynamodb;

import dagger.Component;
import jakarta.inject.Singleton;
import java.util.function.Supplier;
import org.example.age.api.testing.TestModels;
import org.example.age.module.store.dynamodb.testing.DynamoDbTestContainer;
import org.example.age.module.store.dynamodb.testing.TestDependenciesModule;
import org.example.age.service.module.store.AvsVerifiedUserStore;
import org.example.age.service.module.store.testing.AvsAccountStoreTestTemplate;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class DynamoDbAvsAccountStoreTest extends AvsAccountStoreTestTemplate {

    private static final AvsVerifiedUserStore store = TestComponent.create();

    @RegisterExtension
    private static final DynamoDbTestContainer dynamoDb = new DynamoDbTestContainer();

    @BeforeAll
    public static void setUpContainer() {
        dynamoDb.createAvsAccountStoreTables();
        dynamoDb.createAvsAccount("person", TestModels.createVerifiedUser());
    }

    @Override
    protected AvsVerifiedUserStore store() {
        return store;
    }

    /** Dagger component for {@link AvsVerifiedUserStore}. */
    @Component(modules = {DynamoDbAvsAccountStoreModule.class, TestDependenciesModule.class})
    @Singleton
    interface TestComponent extends Supplier<AvsVerifiedUserStore> {

        static AvsVerifiedUserStore create() {
            return DaggerDynamoDbAvsAccountStoreTest_TestComponent.create().get();
        }
    }
}
