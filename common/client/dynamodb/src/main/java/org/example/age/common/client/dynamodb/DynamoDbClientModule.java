package org.example.age.common.client.dynamodb;

import dagger.Module;
import dagger.Provides;
import jakarta.inject.Singleton;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

/**
 * Dagger module that binds {@link DynamoDbClient}.
 * <p>
 * Depends on an unbound {@link DynamoDbClientConfig}.
 */
@Module
public abstract class DynamoDbClientModule {

    @Provides
    @Singleton
    static DynamoDbClient bindDynamoDbClient(DynamoDbClientConfig config) {
        return DynamoDbClientFactory.create(config);
    }

    DynamoDbClientModule() {}
}
