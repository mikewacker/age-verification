package org.example.age.avs.provider.userlocalizer.demo;

import dagger.BindsInstance;
import dagger.Component;
import jakarta.inject.Singleton;
import java.util.function.Supplier;
import org.example.age.common.api.crypto.SecureId;
import org.example.age.site.spi.SiteVerifiedUserLocalizer;
import org.example.age.testing.site.spi.SiteUserLocalizerTestTemplate;

public final class DemoSiteUserLocalizerTest extends SiteUserLocalizerTestTemplate {

    private static final SiteVerifiedUserLocalizer localizer = TestComponent.create();

    @Override
    protected SiteVerifiedUserLocalizer localizer() {
        return localizer;
    }

    @Component(modules = DemoSiteUserLocalizerModule.class)
    @Singleton
    interface TestComponent extends Supplier<SiteVerifiedUserLocalizer> {

        static SiteVerifiedUserLocalizer create() {
            SiteLocalizationKeyConfig config =
                    SiteLocalizationKeyConfig.builder().key(SecureId.generate()).build();
            return DaggerDemoSiteUserLocalizerTest_TestComponent.factory()
                    .create(config)
                    .get();
        }

        @Component.Factory
        interface Factory {

            TestComponent create(@BindsInstance SiteLocalizationKeyConfig config);
        }
    }
}
