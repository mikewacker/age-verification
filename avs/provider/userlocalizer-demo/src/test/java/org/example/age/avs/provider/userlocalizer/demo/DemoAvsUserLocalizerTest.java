package org.example.age.avs.provider.userlocalizer.demo;

import dagger.BindsInstance;
import dagger.Component;
import jakarta.inject.Singleton;
import java.util.Map;
import java.util.function.Supplier;
import org.example.age.avs.spi.AvsVerifiedUserLocalizer;
import org.example.age.common.api.crypto.SecureId;
import org.example.age.testing.site.spi.AvsUserLocalizerTestTemplate;

public final class DemoAvsUserLocalizerTest extends AvsUserLocalizerTestTemplate {

    private static final AvsVerifiedUserLocalizer localizer = TestComponent.create();

    @Override
    protected AvsVerifiedUserLocalizer localizer() {
        return localizer;
    }

    @Component(modules = DemoAvsUserLocalizerModule.class)
    @Singleton
    interface TestComponent extends Supplier<AvsVerifiedUserLocalizer> {

        static AvsVerifiedUserLocalizer create() {
            AvsLocalizationKeysConfig config = AvsLocalizationKeysConfig.builder()
                    .keys(Map.of("site", SecureId.generate(), "other-site", SecureId.generate()))
                    .build();
            return DaggerDemoAvsUserLocalizerTest_TestComponent.factory()
                    .create(config)
                    .get();
        }

        @Component.Factory
        interface Factory {

            TestComponent create(@BindsInstance AvsLocalizationKeysConfig config);
        }
    }
}
