package org.example.age.module.store.redis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.age.testing.WebStageTesting.await;

import dagger.BindsInstance;
import dagger.Component;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.util.Optional;
import org.example.age.api.VerifiedUser;
import org.example.age.module.store.redis.testing.TestDependenciesModule;
import org.example.age.service.module.store.AvsVerifiedUserStore;
import org.example.age.testing.RedisExtension;
import org.example.age.testing.TestModels;
import org.example.age.testing.TestObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class RedisAvsAccountStoreTest {

    @RegisterExtension
    private static final RedisExtension redis = new RedisExtension();

    private static AvsVerifiedUserStore store;

    @BeforeAll
    public static void createAndPopulateAvsVerifiedUserStore() throws IOException {
        // Create.
        TestComponent component = TestComponent.create(redis.port());
        store = component.avsVerifiedUserStore();

        // Populate.
        VerifiedUser user = TestModels.createVerifiedUser();
        String json = TestObjectMapper.get().writeValueAsString(user);
        redis.client().set("age:user:person", json);
    }

    @Test
    public void load() {
        Optional<VerifiedUser> maybeUser = await(store.tryLoad("person"));
        assertThat(maybeUser).isPresent();
    }

    @Test
    public void loadEmpty() {
        Optional<VerifiedUser> maybeUser = await(store.tryLoad("unverified-person"));
        assertThat(maybeUser).isEmpty();
    }

    /** Dagger component for the stores. */
    @Component(modules = {RedisAvsAccountStoreModule.class, TestDependenciesModule.class})
    @Singleton
    interface TestComponent {

        static TestComponent create(int port) {
            return DaggerRedisAvsAccountStoreTest_TestComponent.factory().create(port);
        }

        AvsVerifiedUserStore avsVerifiedUserStore();

        @Component.Factory
        interface Factory {

            TestComponent create(@BindsInstance @Named("port") int port);
        }
    }
}
