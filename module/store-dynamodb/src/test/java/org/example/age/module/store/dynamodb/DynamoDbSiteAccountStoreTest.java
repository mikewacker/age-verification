package org.example.age.module.store.dynamodb;

import dagger.Component;
import jakarta.inject.Singleton;
import java.util.function.Supplier;
import org.example.age.module.store.dynamodb.testing.DynamoDbTestContainer;
import org.example.age.module.store.dynamodb.testing.TestDependenciesModule;
import org.example.age.service.module.store.testing.SiteAccountStoreTestTemplate;
import org.example.age.site.spi.SiteVerificationStore;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class DynamoDbSiteAccountStoreTest extends SiteAccountStoreTestTemplate {

    private static final SiteVerificationStore store = TestComponent.create();

    @RegisterExtension
    private static final DynamoDbTestContainer dynamoDb = new DynamoDbTestContainer();

    @BeforeAll
    public static void setUpContainer() {
        dynamoDb.createSiteAccountStoreTables();
    }

    @Override
    protected SiteVerificationStore store() {
        return store;
    }

    /** Dagger component for {@link SiteVerificationStore} */
    @Component(modules = {DynamoDbSiteAccountStoreModule.class, TestDependenciesModule.class})
    @Singleton
    interface TestComponent extends Supplier<SiteVerificationStore> {

        static SiteVerificationStore create() {
            return DaggerDynamoDbSiteAccountStoreTest_TestComponent.create().get();
        }
    }
}
