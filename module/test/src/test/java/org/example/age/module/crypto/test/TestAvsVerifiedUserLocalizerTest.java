package org.example.age.module.crypto.test;

import dagger.Component;
import jakarta.inject.Singleton;
import java.util.function.Supplier;
import org.example.age.service.module.crypto.AvsVerifiedUserLocalizer;
import org.example.age.service.module.crypto.testing.AvsVerifiedUserLocalizerTestTemplate;

public final class TestAvsVerifiedUserLocalizerTest extends AvsVerifiedUserLocalizerTestTemplate {

    private static final AvsVerifiedUserLocalizer localizer = TestComponent.create();

    @Override
    protected AvsVerifiedUserLocalizer localizer() {
        return localizer;
    }

    /** Dagger component for {@link AvsVerifiedUserLocalizer}. */
    @Component(modules = TestAvsCryptoModule.class)
    @Singleton
    interface TestComponent extends Supplier<AvsVerifiedUserLocalizer> {

        static AvsVerifiedUserLocalizer create() {
            return DaggerTestAvsVerifiedUserLocalizerTest_TestComponent.create().get();
        }
    }
}
