package org.example.age.module.crypto.demo;

import dagger.Component;
import jakarta.inject.Singleton;
import org.example.age.module.crypto.demo.testing.TestDependenciesModule;
import org.example.age.service.module.crypto.SiteVerifiedUserLocalizer;
import org.example.age.service.module.crypto.testing.SiteVerifiedUserLocalizerTestTemplate;
import org.junit.jupiter.api.BeforeAll;

public final class DemoSiteVerifiedUserLocalizerTest extends SiteVerifiedUserLocalizerTestTemplate {

    private static SiteVerifiedUserLocalizer localizer;

    @BeforeAll
    public static void createVerifiedUserLocalizer() {
        TestComponent component = TestComponent.create();
        localizer = component.verifiedUserLocalizer();
    }

    @Override
    protected SiteVerifiedUserLocalizer localizer() {
        return localizer;
    }

    /** Dagger component for crypto. */
    @Component(modules = {DemoSiteCryptoModule.class, TestDependenciesModule.class})
    @Singleton
    interface TestComponent {

        static TestComponent create() {
            return DaggerDemoSiteVerifiedUserLocalizerTest_TestComponent.create();
        }

        SiteVerifiedUserLocalizer verifiedUserLocalizer();
    }
}
