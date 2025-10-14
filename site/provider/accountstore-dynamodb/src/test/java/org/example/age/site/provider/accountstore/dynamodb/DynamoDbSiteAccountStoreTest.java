package org.example.age.site.provider.accountstore.dynamodb;

import dagger.BindsInstance;
import dagger.Component;
import jakarta.inject.Singleton;
import java.util.function.Supplier;
import org.example.age.common.client.dynamodb.DynamoDbClientConfig;
import org.example.age.site.spi.SiteVerifiedAccountStore;
import org.example.age.testing.client.TestClient;
import org.example.age.testing.env.TestEnvModule;
import org.example.age.testing.site.spi.SiteAccountStoreTestTemplate;
import software.amazon.awssdk.regions.Region;

public final class DynamoDbSiteAccountStoreTest extends SiteAccountStoreTestTemplate {

    private static final SiteVerifiedAccountStore store = TestComponent.create();

    @Override
    protected SiteVerifiedAccountStore store() {
        return store;
    }

    /** Dagger component for {@link SiteVerifiedAccountStore} */
    @Component(modules = {DynamoDbSiteAccountStoreModule.class, TestEnvModule.class})
    @Singleton
    interface TestComponent extends Supplier<SiteVerifiedAccountStore> {

        static SiteVerifiedAccountStore create() {
            DynamoDbClientConfig config = DynamoDbClientConfig.builder()
                    .region(Region.US_EAST_1.toString())
                    .testEndpointOverride(TestClient.localhostUri(3000))
                    .build();
            return DaggerDynamoDbSiteAccountStoreTest_TestComponent.factory()
                    .create(config)
                    .get();
        }

        @Component.Factory
        interface Factory {

            TestComponent create(@BindsInstance DynamoDbClientConfig config);
        }
    }
}
