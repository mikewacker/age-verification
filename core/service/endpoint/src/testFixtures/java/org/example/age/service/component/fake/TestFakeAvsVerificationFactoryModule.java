package org.example.age.service.component.fake;

import dagger.Module;
import org.example.age.module.key.test.TestKeyModule;
import org.example.age.module.store.test.TestAvsVerificationStoreModule;
import org.example.age.service.verification.internal.FakeAvsVerificationFactory;
import org.example.age.service.verification.internal.FakeAvsVerificationFactoryModule;

/** Dagger module that binds dependencies for {@link FakeAvsVerificationFactory}. */
@Module(includes = {FakeAvsVerificationFactoryModule.class, TestAvsVerificationStoreModule.class, TestKeyModule.class})
interface TestFakeAvsVerificationFactoryModule {}
