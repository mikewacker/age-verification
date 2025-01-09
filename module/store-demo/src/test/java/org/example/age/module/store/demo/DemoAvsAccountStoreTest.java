package org.example.age.module.store.demo;

import static org.assertj.core.api.Assertions.assertThat;

import dagger.Component;
import jakarta.inject.Singleton;
import java.util.Optional;
import org.example.age.api.VerifiedUser;
import org.example.age.module.store.demo.testing.TestDependenciesModule;
import org.example.age.service.api.store.AvsVerifiedUserStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public final class DemoAvsAccountStoreTest {

    private AvsVerifiedUserStore store;

    @BeforeEach
    public void createVerifiedUserStore() {
        TestComponent component = TestComponent.create();
        store = component.verifiedUserStore();
    }

    @Test
    public void load() throws Exception {
        Optional<VerifiedUser> maybeUser =
                store.tryLoad("person").toCompletableFuture().get();
        assertThat(maybeUser).isPresent();
    }

    @Test
    public void loadEmpty() throws Exception {
        Optional<VerifiedUser> maybeUser =
                store.tryLoad("unverified-person").toCompletableFuture().get();
        assertThat(maybeUser).isEmpty();
    }

    /** Dagger component for the store. */
    @Component(modules = {DemoAvsAccountStoreModule.class, TestDependenciesModule.class})
    @Singleton
    interface TestComponent {

        static TestComponent create() {
            return DaggerDemoAvsAccountStoreTest_TestComponent.create();
        }

        AvsVerifiedUserStore verifiedUserStore();
    }
}
