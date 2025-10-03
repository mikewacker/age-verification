package org.example.age.module.crypto.demo;

import dagger.Component;
import jakarta.inject.Singleton;
import java.util.function.Supplier;
import org.example.age.avs.spi.AvsVerifiedUserLocalizer;
import org.example.age.module.crypto.demo.testing.TestDependenciesModule;
import org.example.age.service.module.crypto.testing.AvsVerifiedUserLocalizerTestTemplate;

public final class DemoAvsVerifiedUserLocalizerTest extends AvsVerifiedUserLocalizerTestTemplate {

    private static final AvsVerifiedUserLocalizer localizer = TestComponent.create();

    @Override
    protected AvsVerifiedUserLocalizer localizer() {
        return localizer;
    }

    /** Dagger component for {@link AvsVerifiedUserLocalizer}. */
    @Component(modules = {DemoAvsCryptoModule.class, TestDependenciesModule.class})
    @Singleton
    interface TestComponent extends Supplier<AvsVerifiedUserLocalizer> {

        static AvsVerifiedUserLocalizer create() {
            return DaggerDemoAvsVerifiedUserLocalizerTest_TestComponent.create().get();
        }
    }
}
