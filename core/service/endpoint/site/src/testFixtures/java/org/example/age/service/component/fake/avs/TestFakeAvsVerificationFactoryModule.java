package org.example.age.service.component.fake.avs;

import dagger.Module;
import org.example.age.module.key.test.common.TestKeyModule;
import org.example.age.module.store.test.avs.TestAvsVerificationStoreModule;
import org.example.age.service.verification.internal.avs.FakeAvsVerificationFactory;
import org.example.age.service.verification.internal.avs.FakeAvsVerificationFactoryModule;

/** Dagger module that binds dependencies for {@link FakeAvsVerificationFactory}. */
@Module(includes = {FakeAvsVerificationFactoryModule.class, TestAvsVerificationStoreModule.class, TestKeyModule.class})
interface TestFakeAvsVerificationFactoryModule {}
