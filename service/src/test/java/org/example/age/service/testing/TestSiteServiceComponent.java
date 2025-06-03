package org.example.age.service.testing;

import dagger.BindsInstance;
import dagger.Component;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import org.example.age.api.SiteApi;
import org.example.age.api.client.AvsApi;
import org.example.age.service.SiteServiceModule;
import org.example.age.service.testing.crypto.TestSiteCryptoModule;
import org.example.age.service.testing.request.TestAccountId;
import org.example.age.service.testing.request.TestRequestModule;
import org.example.age.service.testing.store.TestSiteStoreModule;

/** Dagger component for the service on the site. */
@Component(
        modules = {
            SiteServiceModule.class,
            TestRequestModule.class,
            TestSiteStoreModule.class,
            TestSiteCryptoModule.class,
            TestSiteDependenciesModule.class,
        })
@Singleton
public interface TestSiteServiceComponent {

    static TestSiteServiceComponent create(AvsApi avsClient) {
        return DaggerTestSiteServiceComponent.factory().create(avsClient);
    }

    @Named("testService")
    SiteApi service();

    TestAccountId accountId();

    @Component.Factory
    interface Factory {

        TestSiteServiceComponent create(@BindsInstance @Named("client") AvsApi avsClient);
    }
}
