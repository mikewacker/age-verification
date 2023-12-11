package org.example.age.service.avs.verification.internal.test;

import dagger.Module;
import org.example.age.module.key.common.test.TestKeyModule;
import org.example.age.service.avs.verification.internal.FakeAvsVerificationFactory;
import org.example.age.service.avs.verification.internal.FakeAvsVerificationFactoryModule;

/** Dagger module that binds dependencies for {@link FakeAvsVerificationFactory}. */
@Module(includes = {FakeAvsVerificationFactoryModule.class, TestKeyModule.class})
public interface TestFakeAvsVerificationFactoryModule {}
