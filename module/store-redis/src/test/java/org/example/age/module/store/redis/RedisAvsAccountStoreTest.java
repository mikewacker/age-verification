package org.example.age.module.store.redis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.age.common.testing.WebStageTesting.await;

import dagger.Component;
import jakarta.inject.Singleton;
import java.util.Optional;
import org.example.age.api.VerifiedUser;
import org.example.age.module.store.redis.testing.TestDependenciesModule;
import org.example.age.service.module.store.AvsVerifiedUserStore;
import org.example.age.testing.TestModels;
import org.example.age.testing.containers.RedisTestUtils;
import org.example.age.testing.containers.TestContainers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class RedisAvsAccountStoreTest {

    @RegisterExtension
    private static final TestContainers containers = new TestContainers();

    private static AvsVerifiedUserStore store;

    @BeforeAll
    public static void createAvsVerifiedUserStore() {
        TestComponent component = TestComponent.create();
        store = component.avsVerifiedUserStore();
    }

    @BeforeAll
    public static void createAvsAccount() {
        RedisTestUtils.createAvsAccount(containers.redisClient(), "person", TestModels.createVerifiedUser());
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

    /** Dagger component for the stores. */
    @Component(modules = {RedisAvsAccountStoreModule.class, TestDependenciesModule.class})
    @Singleton
    interface TestComponent {

        static TestComponent create() {
            return DaggerRedisAvsAccountStoreTest_TestComponent.create();
        }

        AvsVerifiedUserStore avsVerifiedUserStore();
    }
}
