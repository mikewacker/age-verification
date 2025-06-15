package org.example.age.service.testing;

import dagger.BindsInstance;
import dagger.Component;
import jakarta.inject.Singleton;
import java.util.function.Supplier;
import org.example.age.module.crypto.test.TestAvsCryptoModule;
import org.example.age.module.store.test.TestAvsAccountStoreModule;
import org.example.age.module.store.test.TestPendingStoreModule;
import org.example.age.service.AvsServiceConfig;
import org.example.age.service.AvsServiceModule;
import org.example.age.service.module.client.SiteClientRepository;
import org.example.age.service.testing.request.TestRequestModule;

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

    static TestAvsService create(SiteClientRepository siteClients) {
        return DaggerTestAvsServiceComponent.factory()
                .create(siteClients, TestConfig.avsService())
                .get();
    }

    @Component.Factory
    interface Factory {

        TestAvsServiceComponent create(
                @BindsInstance SiteClientRepository siteClients, @BindsInstance AvsServiceConfig config);
    }
}
