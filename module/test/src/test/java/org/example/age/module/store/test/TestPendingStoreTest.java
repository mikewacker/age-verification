package org.example.age.module.store.test;

import dagger.Component;
import jakarta.inject.Singleton;
import java.util.function.Supplier;
import org.example.age.service.module.store.PendingStore;
import org.example.age.service.module.store.PendingStoreRepository;
import org.example.age.service.module.store.testing.PendingStoreTestTemplate;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public final class TestPendingStoreTest extends PendingStoreTestTemplate {

    private static final PendingStoreRepository stores = TestComponent.create();

    @Disabled
    @Test
    @Override
    public void putThenExpireThenGet() {}

    @Disabled
    @Test
    @Override
    public void putExpiredThenGet() {}

    @Override
    protected PendingStore<Integer> store() {
        return stores.get("name", Integer.class);
    }

    /** Dagger component for {@link PendingStoreRepository}. */
    @Component(modules = TestPendingStoreModule.class)
    @Singleton
    interface TestComponent extends Supplier<PendingStoreRepository> {

        static PendingStoreRepository create() {
            return DaggerTestPendingStoreTest_TestComponent.create().get();
        }
    }
}
