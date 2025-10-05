package org.example.age.module.store.redis;

import dagger.Component;
import jakarta.inject.Singleton;
import java.util.function.Supplier;
import org.example.age.avs.spi.AvsVerifiedUserStore;
import org.example.age.module.store.redis.testing.RedisTestContainer;
import org.example.age.module.store.redis.testing.TestDependenciesModule;
import org.example.age.testing.api.TestModels;
import org.example.age.testing.site.spi.AvsAccountStoreTestTemplate;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class RedisAvsAccountStoreTest extends AvsAccountStoreTestTemplate {

    private static final AvsVerifiedUserStore store = TestComponent.create();

    @RegisterExtension
    private static final RedisTestContainer redis = new RedisTestContainer();

    @BeforeAll
    public static void setUpContainer() {
        redis.createAvsAccount("person", TestModels.createVerifiedUser());
    }

    @Override
    protected AvsVerifiedUserStore store() {
        return store;
    }

    /** Dagger component for {@link AvsVerifiedUserStore}. */
    @Component(modules = {RedisAvsAccountStoreModule.class, TestDependenciesModule.class})
    @Singleton
    interface TestComponent extends Supplier<AvsVerifiedUserStore> {

        static AvsVerifiedUserStore create() {
            return DaggerRedisAvsAccountStoreTest_TestComponent.create().get();
        }
    }
}
