package org.example.age.module.store.dynamodb.client;

import dagger.Module;
import dagger.Provides;
import jakarta.inject.Singleton;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

/**
 * Dagger module that binds {@link DynamoDbAsyncClient}.
 * <p>
 * Depends on an unbound {@link DynamoDbConfig}.
 */
@Module
public interface DynamoDbClientModule {

    @Provides
    @Singleton
    static DynamoDbClient bindDynamoDbClient(DynamoDbConfig config) {
        return DynamoDbClientFactory.create(config);
    }
}
