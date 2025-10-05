package org.example.age.avs.provider.accountstore.redis;

import dagger.BindsInstance;
import dagger.Component;
import jakarta.inject.Singleton;
import java.util.function.Supplier;
import org.example.age.avs.spi.AvsVerifiedUserStore;
import org.example.age.common.client.redis.RedisClientConfig;
import org.example.age.testing.client.TestClient;
import org.example.age.testing.env.TestEnvModule;
import org.example.age.testing.site.spi.AvsAccountStoreTestTemplate;

public final class RedisAvsAccountStoreTest extends AvsAccountStoreTestTemplate {

    private static final AvsVerifiedUserStore store = TestComponent.create();

    @Override
    protected AvsVerifiedUserStore store() {
        return store;
    }

    /** Dagger component for {@link AvsVerifiedUserStore}. */
    @Component(modules = {RedisAvsAccountStoreModule.class, TestEnvModule.class})
    @Singleton
    interface TestComponent extends Supplier<AvsVerifiedUserStore> {

        static AvsVerifiedUserStore create() {
            RedisClientConfig config = RedisClientConfig.builder()
                    .url(TestClient.localhostUrl(6379))
                    .build();
            return DaggerRedisAvsAccountStoreTest_TestComponent.factory()
                    .create(config)
                    .get();
        }

        @Component.Factory
        interface Factory {

            TestComponent create(@BindsInstance RedisClientConfig config);
        }
    }
}
