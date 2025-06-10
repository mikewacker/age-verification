package org.example.age.module.crypto.demo;

import dagger.Component;
import jakarta.inject.Singleton;
import java.util.function.Supplier;
import org.example.age.module.crypto.demo.testing.TestDependenciesModule;
import org.example.age.service.module.crypto.SiteVerifiedUserLocalizer;
import org.example.age.service.module.crypto.testing.SiteVerifiedUserLocalizerTestTemplate;

public final class DemoSiteVerifiedUserLocalizerTest extends SiteVerifiedUserLocalizerTestTemplate {

    private static final SiteVerifiedUserLocalizer localizer = TestComponent.create();

    @Override
    protected SiteVerifiedUserLocalizer localizer() {
        return localizer;
    }

    /** Dagger component for {@link SiteVerifiedUserLocalizer}. */
    @Component(modules = {DemoSiteCryptoModule.class, TestDependenciesModule.class})
    @Singleton
    interface TestComponent extends Supplier<SiteVerifiedUserLocalizer> {

        static SiteVerifiedUserLocalizer create() {
            return DaggerDemoSiteVerifiedUserLocalizerTest_TestComponent.create()
                    .get();
        }
    }
}
