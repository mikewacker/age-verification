package org.example.age.service.testing;

import dagger.BindsInstance;
import dagger.Component;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.util.function.Supplier;
import org.example.age.api.client.AvsApi;
import org.example.age.module.crypto.test.TestSiteCryptoModule;
import org.example.age.service.SiteServiceConfig;
import org.example.age.service.SiteServiceModule;
import org.example.age.service.testing.request.TestRequestModule;
import org.example.age.service.testing.store.TestSiteStoreModule;

/** Dagger component for {@link TestSiteService} */
@Component(
        modules = {
            SiteServiceModule.class,
            TestRequestModule.class,
            TestSiteStoreModule.class,
            TestSiteCryptoModule.class,
        })
@Singleton
public interface TestSiteServiceComponent extends Supplier<TestSiteService> {

    static TestSiteService create(AvsApi avsClient) {
        return DaggerTestSiteServiceComponent.factory()
                .create(avsClient, TestConfig.siteService())
                .get();
    }

    @Component.Factory
    interface Factory {

        TestSiteServiceComponent create(
                @BindsInstance @Named("client") AvsApi avsClient, @BindsInstance SiteServiceConfig config);
    }
}
