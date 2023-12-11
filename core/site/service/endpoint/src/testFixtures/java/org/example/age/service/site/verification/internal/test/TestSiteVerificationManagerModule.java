package org.example.age.service.site.verification.internal.test;

import dagger.Module;
import org.example.age.module.config.site.test.StubSiteConfigModule;
import org.example.age.module.key.common.test.TestKeyModule;
import org.example.age.module.store.common.inmemory.InMemoryPendingStoreFactoryModule;
import org.example.age.module.store.common.inmemory.InMemoryVerificationStoreModule;
import org.example.age.service.site.verification.internal.SiteVerificationManager;
import org.example.age.service.site.verification.internal.SiteVerificationManagerModule;

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
