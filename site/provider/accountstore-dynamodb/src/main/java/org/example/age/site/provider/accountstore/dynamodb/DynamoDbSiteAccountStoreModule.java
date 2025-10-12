package org.example.age.site.provider.accountstore.dynamodb;

import dagger.Binds;
import dagger.Module;
import org.example.age.common.client.dynamodb.DynamoDbClientConfig;
import org.example.age.common.client.dynamodb.DynamoDbClientModule;
import org.example.age.common.env.BaseEnvModule;
import org.example.age.common.env.LiteEnv;
import org.example.age.site.spi.SiteVerifiedAccountStore;

/**
 * Dagger module that binds {@link SiteVerifiedAccountStore}.
 * <p>
 * Depends on an unbound...
 * <ul>
 *     <li>{@link DynamoDbClientConfig}
 *     <li>{@link LiteEnv}
 * </ul>
 */
@Module(includes = {DynamoDbClientModule.class, BaseEnvModule.class})
public abstract class DynamoDbSiteAccountStoreModule {

    @Binds
    abstract SiteVerifiedAccountStore bindSiteVerifiedAccountStore(DynamoDbSiteVerifiedAccountStore impl);

    DynamoDbSiteAccountStoreModule() {}
}
