package org.example.age.service.verification.internal.avs.test;

import dagger.Module;
import org.example.age.module.config.avs.test.TestAvsConfigModule;
import org.example.age.module.key.common.test.TestKeyModule;
import org.example.age.module.store.avs.test.TestAvsVerificationStoreModule;
import org.example.age.module.store.common.inmemory.InMemoryPendingStoreFactoryModule;
import org.example.age.service.verification.internal.avs.AvsVerificationManager;
import org.example.age.service.verification.internal.avs.AvsVerificationManagerModule;

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
