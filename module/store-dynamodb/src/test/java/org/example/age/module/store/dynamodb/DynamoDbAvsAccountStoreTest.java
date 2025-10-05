package org.example.age.module.store.dynamodb;

import dagger.Component;
import jakarta.inject.Singleton;
import java.util.function.Supplier;
import org.example.age.avs.spi.AvsVerifiedUserStore;
import org.example.age.module.store.dynamodb.testing.DynamoDbTestContainer;
import org.example.age.module.store.dynamodb.testing.TestDependenciesModule;
import org.example.age.testing.api.TestModels;
import org.example.age.testing.site.spi.AvsAccountStoreTestTemplate;
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
