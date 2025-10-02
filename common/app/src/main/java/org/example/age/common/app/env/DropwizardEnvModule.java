package org.example.age.common.app.env;

import dagger.Binds;
import dagger.Module;
import io.dropwizard.core.setup.Environment;
import org.example.age.common.env.LiteEnv;

/**
 * Dagger module that binds {@link LiteEnv}.
 * <p>
 * Depends on an unbound {@link Environment}.
 */
@Module
public abstract class DropwizardEnvModule {

    @Binds
    abstract LiteEnv bindLiteEnv(DropwizardLiteEnv impl);

    DropwizardEnvModule() {}
}
