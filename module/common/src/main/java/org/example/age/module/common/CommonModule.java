package org.example.age.module.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Binds;
import dagger.Module;
import java.util.concurrent.ExecutorService;

/**
 * Dagger module that binds...
 * <ul>
 *     <li>{@link RequestContextProvider}
 *     <li>{@link EnvUtils}
 * </ul>
 * <p>
 * Depends on an unbound...
 * <ul>
 *     <li>{@link ObjectMapper}
 *     <li><code>@Named("worker") {@link ExecutorService}</code>
 * </ul>
 */
@Module
public interface CommonModule {

    @Binds
    RequestContextProvider bindRequestContextProvider(RequestContextFilter impl);

    @Binds
    EnvUtils bindEnvUtils(EnvUtilsImpl impl);
}
