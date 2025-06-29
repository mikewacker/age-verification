package org.example.age.module.common.testing;

import dagger.Binds;
import dagger.BindsOptionalOf;
import dagger.Module;
import org.example.age.module.common.LiteEnv;

/**
 * Dagger module that binds {@link LiteEnv}.
 * <p>
 * Depends on an unbound optional {@link TestComponentRegistrar}.
 * <p>
 * The worker has a single thread.
 */
@Module
public interface TestLiteEnvModule {

    @Binds
    LiteEnv bindLiteEnv(TestLiteEnv impl);

    @BindsOptionalOf
    TestComponentRegistrar bindOptionalTestComponentRegistrar();
}
