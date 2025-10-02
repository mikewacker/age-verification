package org.example.age.testing.env;

import dagger.Binds;
import dagger.Module;
import org.example.age.common.env.LiteEnv;

/**
 * Dagger module that binds {@link LiteEnv}.
 * <p>
 * The worker has a single thread.
 */
@Module
public abstract class TestLiteEnvModule {

    @Binds
    abstract LiteEnv bindLiteEnv(TestLiteEnv impl);

    TestLiteEnvModule() {}
}
