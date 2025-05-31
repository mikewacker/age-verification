package org.example.age.module.store.dynamodb;

import dagger.Component;
import jakarta.inject.Singleton;
import org.example.age.module.store.dynamodb.testing.DynamoDbTestContainer;
import org.example.age.module.store.dynamodb.testing.TestDependenciesModule;
import org.example.age.service.module.store.SiteVerificationStore;
import org.example.age.service.module.store.testing.SiteAccountStoreTestTemplate;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class DynamoDbSiteAccountStoreTest extends SiteAccountStoreTestTemplate {

    @RegisterExtension
    private static final DynamoDbTestContainer dynamoDb = new DynamoDbTestContainer();

    private static SiteVerificationStore store;

    @BeforeAll
    public static void createSiteVerificationStore() {
        TestComponent component = TestComponent.create();
        store = component.siteVerificationStore();
    }

    @BeforeAll
    public static void setUpContainer() {
        dynamoDb.createSiteAccountStoreTables();
    }

    @Override
    protected SiteVerificationStore store() {
        return store;
    }

    /** Dagger component for the store. */
    @Component(modules = {DynamoDbSiteAccountStoreModule.class, TestDependenciesModule.class})
    @Singleton
    interface TestComponent {

        static TestComponent create() {
            return DaggerDynamoDbSiteAccountStoreTest_TestComponent.create();
        }

        SiteVerificationStore siteVerificationStore();
    }
}
