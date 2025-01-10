package org.example.age.module.store.inmemory;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Binds;
import dagger.Module;
import java.util.concurrent.ScheduledExecutorService;
import org.example.age.service.api.store.PendingStoreRepository;

/**
 * Dagger module that binds {@link PendingStoreRepository}.
 * <p>
 * Depends on an unbound...
 * <ul>
 *     <li>{@link ObjectMapper}
 *     <li>{@link ScheduledExecutorService}
 * </ul>
 * <p>
 * Requires sticky sessions to work in a distributed environment.
 */
@Module
public interface InMemoryPendingStoreModule {

    @Binds
    PendingStoreRepository bindPendingStoreRepository(InMemoryPendingStoreRepository impl);
}
