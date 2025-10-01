package org.example.age.common.env;

import dagger.Binds;
import dagger.Module;

/**
 * Dagger module that binds...
 * <ul>
 *     <li>{@link JsonMapper}
 *     <li>{@link Worker}
 * </ul>
 * <p>
 * Depends on an unbound {@link LiteEnv}.
 */
@Module
public interface EnvModule {

    @Binds
    JsonMapper bindJsonMapper(JsonMapperImpl impl);

    @Binds
    Worker bindWorker(WorkerImpl impl);
}
