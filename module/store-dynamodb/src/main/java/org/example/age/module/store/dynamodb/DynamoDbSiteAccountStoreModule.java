package org.example.age.module.store.dynamodb;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Binds;
import dagger.Module;
import java.util.concurrent.ExecutorService;
import org.example.age.module.store.dynamodb.client.DynamoDbClientModule;
import org.example.age.module.store.dynamodb.client.DynamoDbConfig;
import org.example.age.service.module.store.SiteVerificationStore;

/**
 * Dagger module that binds {@link SiteVerificationStore}.
 * <p>
 * Depends on an unbound...
 * <ul>
 *     <li>{@link DynamoDbConfig}
 *     <li>{@link ObjectMapper}
 *     <li><code>@Named("worker") {@link ExecutorService}</code>
 * </ul>
 */
@Module(includes = DynamoDbClientModule.class)
public interface DynamoDbSiteAccountStoreModule {

    @Binds
    SiteVerificationStore bindSiteVerificationStore(DynamoDbSiteVerificationStore impl);
}
