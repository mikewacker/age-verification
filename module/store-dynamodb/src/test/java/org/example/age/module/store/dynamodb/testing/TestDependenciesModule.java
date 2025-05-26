package org.example.age.module.store.dynamodb.testing;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;
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
 */
@Module(includes = TestEnvModule.class)
public interface TestDependenciesModule {

    @Provides
    static DynamoDbConfig provideDynamoDbConfig() {
        return TestConfig.dynamoDb();
    }
}
