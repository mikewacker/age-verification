package org.example.age.module.common;

import dagger.Binds;
import dagger.Module;

/**
 * Dagger module that binds...
 * <ul>
 *     <li>{@link RequestContextProvider}
 *     <li>{@link JsonMapper}
 *     <li>{@link Worker}
 * </ul>
 * <p>
 * Depends on an unbound {@link LiteEnv}.
 */
@Module
public interface CommonModule {

    @Binds
    RequestContextProvider bindRequestContextProvider(RequestContextFilter impl);

    @Binds
    JsonMapper bindJsonMapper(JsonMapperImpl impl);

    @Binds
    Worker bindWorker(WorkerImpl impl);
}
