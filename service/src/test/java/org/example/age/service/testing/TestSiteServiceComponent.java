package org.example.age.service.testing;

import dagger.BindsInstance;
import dagger.Component;
import jakarta.inject.Singleton;
import java.time.Duration;
import java.util.function.Supplier;
import org.example.age.avs.api.client.AvsApi;
import org.example.age.common.provider.pendingstore.test.TestPendingStoreModule;
import org.example.age.module.crypto.test.TestSiteCryptoModule;
import org.example.age.module.request.test.TestRequestModule;
import org.example.age.module.store.test.TestSiteAccountStoreModule;
import org.example.age.site.endpoint.SiteEndpointConfig;
import org.example.age.site.endpoint.SiteEndpointModule;

/** Dagger component for {@link TestSiteService} */
@Component(
        modules = {
            SiteEndpointModule.class,
            TestRequestModule.class,
            TestSiteAccountStoreModule.class,
            TestPendingStoreModule.class,
            TestSiteCryptoModule.class,
        })
@Singleton
public interface TestSiteServiceComponent extends Supplier<TestSiteService> {

    static TestSiteService create(AvsApi avsClient) {
        SiteEndpointConfig config = SiteEndpointConfig.builder()
                .id("site")
                .verifiedAccountExpiresIn(Duration.ofDays(30))
                .build();
        return DaggerTestSiteServiceComponent.factory()
                .create(avsClient, config)
                .get();
    }

    @Component.Factory
    interface Factory {

        TestSiteServiceComponent create(@BindsInstance AvsApi avsClient, @BindsInstance SiteEndpointConfig config);
    }
}
