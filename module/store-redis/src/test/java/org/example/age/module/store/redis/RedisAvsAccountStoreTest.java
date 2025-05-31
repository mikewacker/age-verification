package org.example.age.module.store.redis;

import dagger.Component;
import jakarta.inject.Singleton;
import org.example.age.api.testing.TestModels;
import org.example.age.module.store.redis.testing.RedisTestContainer;
import org.example.age.module.store.redis.testing.TestDependenciesModule;
import org.example.age.service.module.store.AvsVerifiedUserStore;
import org.example.age.service.module.store.testing.AvsAccountStoreTestTemplate;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class RedisAvsAccountStoreTest extends AvsAccountStoreTestTemplate {

    @RegisterExtension
    private static final RedisTestContainer redis = new RedisTestContainer();

    private static AvsVerifiedUserStore store;

    @BeforeAll
    public static void createAvsVerifiedUserStore() {
        TestComponent component = TestComponent.create();
        store = component.avsVerifiedUserStore();
    }

    @BeforeAll
    public static void setUpContainer() {
        redis.createAvsAccount("person", TestModels.createVerifiedUser());
    }

    @Override
    protected AvsVerifiedUserStore store() {
        return store;
    }

    /** Dagger component for the store. */
    @Component(modules = {RedisAvsAccountStoreModule.class, TestDependenciesModule.class})
    @Singleton
    interface TestComponent {

        static TestComponent create() {
            return DaggerRedisAvsAccountStoreTest_TestComponent.create();
        }

        AvsVerifiedUserStore avsVerifiedUserStore();
    }
}
