package org.example.age.module.store.dynamodb;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.age.common.testing.WebStageTesting.await;

import dagger.Component;
import jakarta.inject.Singleton;
import java.util.Optional;
import org.example.age.api.VerifiedUser;
import org.example.age.api.testing.TestModels;
import org.example.age.module.store.dynamodb.testing.DynamoDbTestContainer;
import org.example.age.module.store.dynamodb.testing.TestDependenciesModule;
import org.example.age.service.module.store.AvsVerifiedUserStore;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class DynamoDbAvsAccountStoreTest {

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

    @Test
    public void load() {
        Optional<VerifiedUser> maybeUser = await(store.tryLoad("person"));
        assertThat(maybeUser).isPresent();
    }

    @Test
    public void load_Empty() {
        Optional<VerifiedUser> maybeUser = await(store.tryLoad("unverified-person"));
        assertThat(maybeUser).isEmpty();
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
