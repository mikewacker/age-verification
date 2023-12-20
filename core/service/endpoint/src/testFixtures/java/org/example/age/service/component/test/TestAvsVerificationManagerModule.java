package org.example.age.service.component.test;

import dagger.Module;
import org.example.age.module.config.test.TestAvsConfigModule;
import org.example.age.module.key.test.TestKeyModule;
import org.example.age.module.store.inmemory.InMemoryPendingStoreFactoryModule;
import org.example.age.module.store.test.TestAvsVerificationStoreModule;
import org.example.age.service.verification.internal.AvsVerificationManager;
import org.example.age.service.verification.internal.AvsVerificationManagerModule;

/** Dagger module that binds dependencies for {@link AvsVerificationManager}. */
@Module(
        includes = {
            AvsVerificationManagerModule.class,
            TestAvsVerificationStoreModule.class,
            InMemoryPendingStoreFactoryModule.class,
            TestKeyModule.class,
            TestAvsConfigModule.class,
        })
interface TestAvsVerificationManagerModule {}
