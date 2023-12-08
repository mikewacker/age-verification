package org.example.age.avs.service.verification.internal.test;

import dagger.Module;
import org.example.age.avs.service.verification.internal.FakeAvsVerificationFactory;
import org.example.age.avs.service.verification.internal.FakeAvsVerificationFactoryModule;
import org.example.age.module.key.common.test.TestKeyModule;

/** Dagger module that binds dependencies for {@link FakeAvsVerificationFactory}. */
@Module(includes = {FakeAvsVerificationFactoryModule.class, TestKeyModule.class})
public interface TestAvsVerificationFactoryModule {}
