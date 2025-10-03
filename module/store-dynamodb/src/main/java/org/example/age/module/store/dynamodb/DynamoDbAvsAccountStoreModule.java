package org.example.age.module.store.dynamodb;

import dagger.Binds;
import dagger.Module;
import org.example.age.common.env.BaseEnvModule;
import org.example.age.common.env.LiteEnv;
import org.example.age.module.store.dynamodb.client.DynamoDbClientModule;
import org.example.age.module.store.dynamodb.client.DynamoDbConfig;
import org.example.age.service.module.store.AvsVerifiedUserStore;

/**
 * Dagger module that binds {@link AvsVerifiedUserStore}.
 * <p>
 * Depends on an unbound...
 * <ul>
 *     <li>{@link DynamoDbConfig}
 *     <li>{@link LiteEnv}
 * </ul>
 */
@Module(includes = {DynamoDbClientModule.class, BaseEnvModule.class})
public interface DynamoDbAvsAccountStoreModule {

    @Binds
    AvsVerifiedUserStore bindAvsVerifiedUserStore(DynamoDbAvsVerifiedUserStore impl);
}
