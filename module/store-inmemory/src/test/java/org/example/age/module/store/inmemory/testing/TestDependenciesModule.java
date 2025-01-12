package org.example.age.module.store.inmemory.testing;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Binds;
import dagger.Module;
import java.util.concurrent.ScheduledExecutorService;
import org.example.age.testing.TestObjectMapper;

/**
 * Dagger module that binds...
 * <ul>
 *     <li>{@link ObjectMapper}
 *     <li>{@link ScheduledExecutorService} (with {@link FakeScheduledExecutorService})
 * </ul>
 */
@Module(includes = TestObjectMapper.Module.class)
public interface TestDependenciesModule {

    @Binds
    ScheduledExecutorService bindScheduledExecutorService(FakeScheduledExecutorService impl);
}
