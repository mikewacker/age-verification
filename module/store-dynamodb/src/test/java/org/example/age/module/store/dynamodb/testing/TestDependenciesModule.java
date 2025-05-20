package org.example.age.module.store.dynamodb.testing;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.util.concurrent.ExecutorService;
import org.example.age.module.store.dynamodb.client.DynamoDbConfig;
import org.example.age.testing.TestEnvModule;

/**
 * Dagger module that binds...
 * <ul>
 *     <li>{@link DynamoDbConfig}
 *     <li>{@link ObjectMapper}
 *     <li><code>@Named {@link ExecutorService}</code>
 * </ul>
 * <p>
 * Depends on an unbound <code>@Named("port") int</code>.
 */
@Module(includes = TestEnvModule.class)
public interface TestDependenciesModule {

    @Provides
    @Singleton
    static DynamoDbConfig provideDynamoDbConfig(@Named("port") int port) {
        return TestConfig.createDynamoDb(port);
    }
}
