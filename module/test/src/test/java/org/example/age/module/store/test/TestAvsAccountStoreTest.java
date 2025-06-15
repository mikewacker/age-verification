package org.example.age.module.store.test;

import dagger.Component;
import jakarta.inject.Singleton;
import java.util.function.Supplier;
import org.example.age.service.module.store.AvsVerifiedUserStore;
import org.example.age.service.module.store.testing.AvsAccountStoreTestTemplate;

public final class TestAvsAccountStoreTest extends AvsAccountStoreTestTemplate {

    private static final AvsVerifiedUserStore store = TestComponent.create();

    @Override
    protected AvsVerifiedUserStore store() {
        return store;
    }

    /** Dagger component for {@link AvsVerifiedUserStore}. */
    @Component(modules = TestAvsAccountStoreModule.class)
    @Singleton
    interface TestComponent extends Supplier<AvsVerifiedUserStore> {

        static AvsVerifiedUserStore create() {
            return DaggerTestAvsAccountStoreTest_TestComponent.create().get();
        }
    }
}
