package org.example.age.avs.provider.accountstore.redis;

import dagger.BindsInstance;
import dagger.Component;
import jakarta.inject.Singleton;
import java.util.function.Supplier;
import org.example.age.avs.spi.AvsVerifiedAccountStore;
import org.example.age.common.client.redis.RedisClientConfig;
import org.example.age.testing.client.TestClient;
import org.example.age.testing.env.TestEnvModule;
import org.example.age.testing.site.spi.AvsAccountStoreTestTemplate;

public final class RedisAvsAccountStoreTest extends AvsAccountStoreTestTemplate {

    private static final AvsVerifiedAccountStore store = TestComponent.create();

    @Override
    protected AvsVerifiedAccountStore store() {
        return store;
    }

    /** Dagger component for {@link AvsVerifiedAccountStore}. */
    @Component(modules = {RedisAvsAccountStoreModule.class, TestEnvModule.class})
    @Singleton
    interface TestComponent extends Supplier<AvsVerifiedAccountStore> {

        static AvsVerifiedAccountStore create() {
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
