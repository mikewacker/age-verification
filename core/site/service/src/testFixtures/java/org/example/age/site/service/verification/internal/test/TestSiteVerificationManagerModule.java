package org.example.age.site.service.verification.internal.test;

import dagger.Module;
import org.example.age.common.service.key.test.TestKeyModule;
import org.example.age.common.service.store.inmemory.InMemoryPendingStoreFactoryModule;
import org.example.age.site.service.config.test.StubSiteConfigModule;
import org.example.age.site.service.store.InMemoryVerificationStoreModule;
import org.example.age.site.service.verification.internal.SiteVerificationManager;
import org.example.age.site.service.verification.internal.SiteVerificationManagerModule;

/** Dagger module that binds dependencies for {@link SiteVerificationManager}. */
@Module(
        includes = {
            SiteVerificationManagerModule.class,
            InMemoryVerificationStoreModule.class,
            InMemoryPendingStoreFactoryModule.class,
            TestKeyModule.class,
            StubSiteConfigModule.class,
        })
public interface TestSiteVerificationManagerModule {}
