package org.example.age.module.store.dynamodb;

import dagger.Binds;
import dagger.Module;
import org.example.age.common.client.dynamodb.DynamoDbClientConfig;
import org.example.age.common.client.dynamodb.DynamoDbClientModule;
import org.example.age.common.env.BaseEnvModule;
import org.example.age.common.env.LiteEnv;
import org.example.age.site.spi.SiteVerificationStore;

/**
 * Dagger module that binds {@link SiteVerificationStore}.
 * <p>
 * Depends on an unbound...
 * <ul>
 *     <li>{@link DynamoDbClientConfig}
 *     <li>{@link LiteEnv}
 * </ul>
 */
@Module(includes = {DynamoDbClientModule.class, BaseEnvModule.class})
public interface DynamoDbSiteAccountStoreModule {

    @Binds
    SiteVerificationStore bindSiteVerificationStore(DynamoDbSiteVerificationStore impl);
}
