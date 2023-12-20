package org.example.age.service.component.test;

import dagger.Module;
import org.example.age.module.config.test.TestSiteConfigModule;
import org.example.age.module.key.test.TestKeyModule;
import org.example.age.module.store.inmemory.InMemoryPendingStoreFactoryModule;
import org.example.age.module.store.inmemory.InMemoryVerificationStoreModule;
import org.example.age.service.verification.internal.SiteVerificationManager;
import org.example.age.service.verification.internal.SiteVerificationManagerModule;

/** Dagger module that binds dependencies for {@link SiteVerificationManager}. */
@Module(
        includes = {
            SiteVerificationManagerModule.class,
            InMemoryVerificationStoreModule.class,
            InMemoryPendingStoreFactoryModule.class,
            TestKeyModule.class,
            TestSiteConfigModule.class,
        })
interface TestSiteVerificationManagerModule {}
