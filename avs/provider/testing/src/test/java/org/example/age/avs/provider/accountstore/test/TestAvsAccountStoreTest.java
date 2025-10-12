package org.example.age.avs.provider.accountstore.test;

import dagger.Component;
import jakarta.inject.Singleton;
import java.util.function.Supplier;
import org.example.age.avs.spi.AvsVerifiedAccountStore;
import org.example.age.testing.site.spi.AvsAccountStoreTestTemplate;

public final class TestAvsAccountStoreTest extends AvsAccountStoreTestTemplate {

    private static final AvsVerifiedAccountStore store = TestComponent.create();

    @Override
    protected AvsVerifiedAccountStore store() {
        return store;
    }

    /** Dagger component for {@link AvsVerifiedAccountStore}. */
    @Component(modules = TestAvsAccountStoreModule.class)
    @Singleton
    interface TestComponent extends Supplier<AvsVerifiedAccountStore> {

        static AvsVerifiedAccountStore create() {
            return DaggerTestAvsAccountStoreTest_TestComponent.create().get();
        }
    }
}
