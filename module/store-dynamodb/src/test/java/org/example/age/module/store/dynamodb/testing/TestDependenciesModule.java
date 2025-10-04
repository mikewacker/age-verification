package org.example.age.module.store.dynamodb.testing;

import dagger.Module;
import dagger.Provides;
import org.example.age.common.client.dynamodb.DynamoDbClientConfig;
import org.example.age.common.env.LiteEnv;
import org.example.age.testing.env.TestEnvModule;

/**
 * Dagger module that binds...
 * <ul>
 *     <li>{@link DynamoDbClientConfig}
 *     <li>{@link LiteEnv}
 * </ul>
 */
@Module(includes = TestEnvModule.class)
public interface TestDependenciesModule {

    @Provides
    static DynamoDbClientConfig provideDynamoDbConfig() {
        return TestConfig.dynamoDb();
    }
}
