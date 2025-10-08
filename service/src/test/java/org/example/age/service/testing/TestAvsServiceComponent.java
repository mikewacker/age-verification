package org.example.age.service.testing;

import dagger.BindsInstance;
import dagger.Component;
import jakarta.inject.Singleton;
import java.time.Duration;
import java.util.Map;
import java.util.function.Supplier;
import org.example.age.avs.endpoint.AvsEndpointConfig;
import org.example.age.avs.endpoint.AvsEndpointModule;
import org.example.age.common.api.AgeThresholds;
import org.example.age.module.crypto.test.TestAvsCryptoModule;
import org.example.age.module.request.test.TestRequestModule;
import org.example.age.module.store.test.TestAvsAccountStoreModule;
import org.example.age.module.store.test.TestPendingStoreModule;
import org.example.age.site.api.client.SiteApi;

/** Dagger component for {@link TestAvsService}. */
@Component(
        modules = {
            AvsEndpointModule.class,
            TestRequestModule.class,
            TestAvsAccountStoreModule.class,
            TestPendingStoreModule.class,
            TestAvsCryptoModule.class,
        })
@Singleton
public interface TestAvsServiceComponent extends Supplier<TestAvsService> {

    static TestAvsService create(Map<String, SiteApi> siteClients) {
        AvsEndpointConfig config = AvsEndpointConfig.builder()
                .verificationRequestExpiresIn(Duration.ofMinutes(5))
                .ageThresholds(Map.of("site1", AgeThresholds.of(18), "site2", AgeThresholds.of(18)))
                .build();
        return DaggerTestAvsServiceComponent.factory()
                .create(siteClients, config)
                .get();
    }

    @Component.Factory
    interface Factory {

        TestAvsServiceComponent create(
                @BindsInstance Map<String, SiteApi> siteClients, @BindsInstance AvsEndpointConfig config);
    }
}
