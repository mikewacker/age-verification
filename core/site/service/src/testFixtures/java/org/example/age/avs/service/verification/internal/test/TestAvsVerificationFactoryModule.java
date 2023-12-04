package org.example.age.avs.service.verification.internal.test;

import dagger.Module;
import org.example.age.avs.service.data.internal.AvsServiceJsonSerializerModule;
import org.example.age.avs.service.verification.internal.FakeAvsVerificationFactory;
import org.example.age.avs.service.verification.internal.FakeAvsVerificationFactoryModule;
import org.example.age.common.service.key.test.TestKeyModule;

/** Dagger module that binds dependencies for {@link FakeAvsVerificationFactory}. */
@Module(includes = {FakeAvsVerificationFactoryModule.class, TestKeyModule.class, AvsServiceJsonSerializerModule.class})
public interface TestAvsVerificationFactoryModule {}
