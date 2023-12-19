package org.example.age.service.component.test.site;

import dagger.Module;
import org.example.age.module.config.site.test.TestSiteConfigModule;
import org.example.age.module.key.common.test.TestKeyModule;
import org.example.age.module.store.common.inmemory.InMemoryPendingStoreFactoryModule;
import org.example.age.module.store.common.inmemory.InMemoryVerificationStoreModule;
import org.example.age.service.verification.internal.site.SiteVerificationManager;
import org.example.age.service.verification.internal.site.SiteVerificationManagerModule;

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
