package org.example.age.service.testing;

import dagger.BindsInstance;
import dagger.Component;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import org.example.age.api.AvsApi;
import org.example.age.service.AvsServiceModule;
import org.example.age.service.module.client.SiteClientRepository;
import org.example.age.service.testing.crypto.TestAvsCryptoModule;
import org.example.age.service.testing.request.TestAccountId;
import org.example.age.service.testing.request.TestRequestModule;
import org.example.age.service.testing.store.TestAvsStoreModule;

/** Dagger component for the service on the age verification service. */
@Component(
        modules = {
            AvsServiceModule.class,
            TestRequestModule.class,
            TestAvsStoreModule.class,
            TestAvsCryptoModule.class,
            TestAvsDependenciesModule.class,
        })
@Singleton
public interface TestAvsServiceComponent {

    static TestAvsServiceComponent create(SiteClientRepository siteClients) {
        return DaggerTestAvsServiceComponent.factory().create(siteClients);
    }

    @Named("testService")
    AvsApi service();

    TestAccountId accountId();

    @Component.Factory
    interface Factory {

        TestAvsServiceComponent create(@BindsInstance SiteClientRepository siteClients);
    }
}
