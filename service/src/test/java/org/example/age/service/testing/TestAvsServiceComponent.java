package org.example.age.service.testing;

import dagger.BindsInstance;
import dagger.Component;
import jakarta.inject.Singleton;
import java.time.Duration;
import java.util.Map;
import java.util.function.Supplier;
import org.example.age.avs.endpoint.AvsEndpointConfig;
import org.example.age.avs.endpoint.AvsEndpointModule;
import org.example.age.avs.provider.accountstore.test.TestAvsAccountStoreModule;
import org.example.age.avs.provider.certificatesigner.test.TestCertificateSignerModule;
import org.example.age.avs.provider.userlocalizer.test.TestAvsUserLocalizerModule;
import org.example.age.common.api.AgeThresholds;
import org.example.age.common.provider.pendingstore.test.TestPendingStoreModule;
import org.example.age.module.request.test.TestRequestModule;
import org.example.age.site.api.client.SiteApi;

/** Dagger component for {@link TestAvsService}. */
@Component(
        modules = {
            AvsEndpointModule.class,
            TestRequestModule.class,
            TestAvsAccountStoreModule.class,
            TestPendingStoreModule.class,
            TestAvsUserLocalizerModule.class,
            TestCertificateSignerModule.class,
        })
@Singleton
public interface TestAvsServiceComponent extends Supplier<TestAvsService> {

    static TestAvsService create(Map<String, SiteApi> siteClients) {
        AvsEndpointConfig config = AvsEndpointConfig.builder()
                .verificationRequestExpiresIn(Duration.ofMinutes(5))
                .ageThresholds(Map.of("site", AgeThresholds.of(18), "other-site", AgeThresholds.of(18)))
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
