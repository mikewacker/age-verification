package org.example.age.avs.provider.accountstore.dynamodb;

import dagger.BindsInstance;
import dagger.Component;
import jakarta.inject.Singleton;
import java.util.function.Supplier;
import org.example.age.avs.spi.AvsVerifiedUserStore;
import org.example.age.common.client.dynamodb.DynamoDbClientConfig;
import org.example.age.testing.client.TestClient;
import org.example.age.testing.env.TestEnvModule;
import org.example.age.testing.site.spi.AvsAccountStoreTestTemplate;
import software.amazon.awssdk.regions.Region;

public final class DynamoDbAvsAccountStoreTest extends AvsAccountStoreTestTemplate {

    private static final AvsVerifiedUserStore store = TestComponent.create();

    @Override
    protected AvsVerifiedUserStore store() {
        return store;
    }

    /** Dagger component for {@link AvsVerifiedUserStore}. */
    @Component(modules = {DynamoDbAvsAccountStoreModule.class, TestEnvModule.class})
    @Singleton
    interface TestComponent extends Supplier<AvsVerifiedUserStore> {

        static AvsVerifiedUserStore create() {
            DynamoDbClientConfig config = DynamoDbClientConfig.builder()
                    .region(Region.US_EAST_1.toString())
                    .testEndpointOverride(TestClient.localhostUri(8000))
                    .build();
            return DaggerDynamoDbAvsAccountStoreTest_TestComponent.factory()
                    .create(config)
                    .get();
        }

        @Component.Factory
        interface Factory {

            TestComponent create(@BindsInstance DynamoDbClientConfig config);
        }
    }
}
