package org.example.age.module.crypto.demo;

import dagger.Component;
import jakarta.inject.Singleton;
import org.example.age.module.crypto.demo.testing.TestDependenciesModule;
import org.example.age.service.module.crypto.AvsVerifiedUserLocalizer;
import org.example.age.service.module.crypto.testing.AvsVerifiedUserLocalizerTestTemplate;
import org.junit.jupiter.api.BeforeAll;

public final class DemoAvsVerifiedUserLocalizerTest extends AvsVerifiedUserLocalizerTestTemplate {

    private static AvsVerifiedUserLocalizer localizer;

    @BeforeAll
    public static void createVerifiedUserLocalizer() {
        TestComponent component = TestComponent.create();
        localizer = component.verifiedUserLocalizer();
    }

    @Override
    protected AvsVerifiedUserLocalizer localizer() {
        return localizer;
    }

    /** Dagger component for crypto. */
    @Component(modules = {DemoAvsCryptoModule.class, TestDependenciesModule.class})
    @Singleton
    interface TestComponent {

        static TestComponent create() {
            return DaggerDemoAvsVerifiedUserLocalizerTest_TestComponent.create();
        }

        AvsVerifiedUserLocalizer verifiedUserLocalizer();
    }
}
