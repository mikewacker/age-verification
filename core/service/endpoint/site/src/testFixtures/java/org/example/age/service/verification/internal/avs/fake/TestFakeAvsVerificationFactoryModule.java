package org.example.age.service.verification.internal.avs.fake;

import dagger.Module;
import org.example.age.module.key.common.test.TestKeyModule;
import org.example.age.module.store.avs.test.TestAvsVerificationStoreModule;
import org.example.age.service.verification.internal.avs.FakeAvsVerificationFactory;
import org.example.age.service.verification.internal.avs.FakeAvsVerificationFactoryModule;

/** Dagger module that binds dependencies for {@link FakeAvsVerificationFactory}. */
@Module(includes = {FakeAvsVerificationFactoryModule.class, TestAvsVerificationStoreModule.class, TestKeyModule.class})
interface TestFakeAvsVerificationFactoryModule {}
