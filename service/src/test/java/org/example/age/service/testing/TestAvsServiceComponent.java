package org.example.age.service.testing;

import dagger.BindsInstance;
import dagger.Component;
import jakarta.inject.Singleton;
import java.util.Map;
import java.util.function.Supplier;
import org.example.age.module.crypto.test.TestAvsCryptoModule;
import org.example.age.module.request.test.TestRequestModule;
import org.example.age.module.store.test.TestAvsAccountStoreModule;
import org.example.age.module.store.test.TestPendingStoreModule;
import org.example.age.service.AvsServiceConfig;
import org.example.age.service.AvsServiceModule;
import org.example.age.site.api.client.SiteApi;

/** Dagger component for {@link TestAvsService}. */
@Component(
        modules = {
            AvsServiceModule.class,
            TestRequestModule.class,
            TestAvsAccountStoreModule.class,
            TestPendingStoreModule.class,
            TestAvsCryptoModule.class,
        })
@Singleton
public interface TestAvsServiceComponent extends Supplier<TestAvsService> {

    static TestAvsService create(Map<String, SiteApi> siteClients) {
        return DaggerTestAvsServiceComponent.factory()
                .create(siteClients, TestConfig.avsService())
                .get();
    }

    @Component.Factory
    interface Factory {

        TestAvsServiceComponent create(
                @BindsInstance Map<String, SiteApi> siteClients, @BindsInstance AvsServiceConfig config);
    }
}
