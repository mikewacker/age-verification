package org.example.age.module.store.dynamodb;

import dagger.Binds;
import dagger.Module;
import org.example.age.module.common.CommonModule;
import org.example.age.module.common.LiteEnv;
import org.example.age.module.store.dynamodb.client.DynamoDbClientModule;
import org.example.age.module.store.dynamodb.client.DynamoDbConfig;
import org.example.age.service.module.store.SiteVerificationStore;

/**
 * Dagger module that binds {@link SiteVerificationStore}.
 * <p>
 * Depends on an unbound...
 * <ul>
 *     <li>{@link DynamoDbConfig}
 *     <li>{@link LiteEnv}
 * </ul>
 */
@Module(includes = {DynamoDbClientModule.class, CommonModule.class})
public interface DynamoDbSiteAccountStoreModule {

    @Binds
    SiteVerificationStore bindSiteVerificationStore(DynamoDbSiteVerificationStore impl);
}
