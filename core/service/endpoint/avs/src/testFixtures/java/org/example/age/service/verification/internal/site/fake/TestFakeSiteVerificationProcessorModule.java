package org.example.age.service.verification.internal.site.fake;

import dagger.Module;
import org.example.age.module.key.common.test.TestKeyModule;
import org.example.age.module.store.common.inmemory.InMemoryVerificationStoreModule;
import org.example.age.service.verification.internal.site.FakeSiteVerificationProcessor;
import org.example.age.service.verification.internal.site.FakeSiteVerificationProcessorModule;

/** Dagger module that binds dependencies for {@link FakeSiteVerificationProcessor}. */
@Module(
        includes = {
            FakeSiteVerificationProcessorModule.class,
            InMemoryVerificationStoreModule.class,
            TestKeyModule.class,
        })
interface TestFakeSiteVerificationProcessorModule {}
