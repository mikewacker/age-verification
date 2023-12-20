package org.example.age.service.component.fake;

import dagger.Module;
import org.example.age.module.key.test.TestKeyModule;
import org.example.age.module.store.inmemory.InMemoryVerificationStoreModule;
import org.example.age.service.verification.internal.FakeSiteVerificationProcessor;
import org.example.age.service.verification.internal.FakeSiteVerificationProcessorModule;

/** Dagger module that binds dependencies for {@link FakeSiteVerificationProcessor}. */
@Module(
        includes = {
            FakeSiteVerificationProcessorModule.class,
            InMemoryVerificationStoreModule.class,
            TestKeyModule.class,
        })
interface TestFakeSiteVerificationProcessorModule {}
