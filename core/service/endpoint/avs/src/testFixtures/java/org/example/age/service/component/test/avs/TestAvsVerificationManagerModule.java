package org.example.age.service.component.test.avs;

import dagger.Module;
import org.example.age.module.config.test.avs.TestAvsConfigModule;
import org.example.age.module.key.test.common.TestKeyModule;
import org.example.age.module.store.test.avs.TestAvsVerificationStoreModule;
import org.example.age.module.store.inmemory.common.InMemoryPendingStoreFactoryModule;
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
