package org.example.age.service.component.fake.site;

import dagger.Module;
import org.example.age.module.key.test.common.TestKeyModule;
import org.example.age.module.store.inmemory.common.InMemoryVerificationStoreModule;
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
