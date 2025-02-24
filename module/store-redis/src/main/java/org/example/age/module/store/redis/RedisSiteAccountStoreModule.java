package org.example.age.module.store.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Binds;
import dagger.Module;
import java.util.concurrent.ExecutorService;
import org.example.age.service.module.store.SiteVerificationStore;

/**
 * Dagger module that binds {@link SiteVerificationStore}.
 * <p>
 * Depends on an unbound...
 * <ul>
 *     <li>{@link RedisConfig}
 *     <li>{@link ObjectMapper}
 *     <li><code>@Named("worker") {@link ExecutorService}</code>
 * </ul>
 */
@Module(includes = RedisClientModule.class)
public interface RedisSiteAccountStoreModule {

    @Binds
    SiteVerificationStore bindSiteVerificationStore(RedisSiteVerificationStore impl);
}
