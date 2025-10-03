package org.example.age.module.crypto.test;

import dagger.Component;
import jakarta.inject.Singleton;
import java.util.function.Supplier;
import org.example.age.service.module.crypto.testing.SiteVerifiedUserLocalizerTestTemplate;
import org.example.age.site.spi.SiteVerifiedUserLocalizer;

public final class TestSiteVerifiedUserLocalizerTest extends SiteVerifiedUserLocalizerTestTemplate {

    private static final SiteVerifiedUserLocalizer localizer = TestComponent.create();

    @Override
    protected SiteVerifiedUserLocalizer localizer() {
        return localizer;
    }

    /** Dagger component for {@link SiteVerifiedUserLocalizer}. */
    @Component(modules = TestSiteCryptoModule.class)
    @Singleton
    interface TestComponent extends Supplier<SiteVerifiedUserLocalizer> {

        static SiteVerifiedUserLocalizer create() {
            return DaggerTestSiteVerifiedUserLocalizerTest_TestComponent.create()
                    .get();
        }
    }
}
