package org.example.age.app.env;

import dagger.Binds;
import dagger.Module;
import io.dropwizard.core.setup.Environment;
import org.example.age.module.common.LiteEnv;

/**
 * Dagger module that binds {@link LiteEnv}.
 * <p>
 * Depends on an unbound {@link Environment}.
 */
@Module
public interface EnvModule {

    @Binds
    LiteEnv bindLiteEnv(DropwizardLiteEnv impl);
}
