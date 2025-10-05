package org.example.age.avs.provider.accountstore.dynamodb;

import dagger.Binds;
import dagger.Module;
import org.example.age.avs.spi.AvsVerifiedUserStore;
import org.example.age.common.client.dynamodb.DynamoDbClientConfig;
import org.example.age.common.client.dynamodb.DynamoDbClientModule;
import org.example.age.common.env.BaseEnvModule;
import org.example.age.common.env.LiteEnv;

/**
 * Dagger module that binds {@link AvsVerifiedUserStore}.
 * <p>
 * Depends on an unbound...
 * <ul>
 *     <li>{@link DynamoDbClientConfig}
 *     <li>{@link LiteEnv}
 * </ul>
 */
@Module(includes = {DynamoDbClientModule.class, BaseEnvModule.class})
public abstract class DynamoDbAvsAccountStoreModule {

    @Binds
    abstract AvsVerifiedUserStore bindAvsVerifiedUserStore(DynamoDbAvsVerifiedUserStore impl);

    DynamoDbAvsAccountStoreModule() {}
}
