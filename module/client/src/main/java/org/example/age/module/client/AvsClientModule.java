package org.example.age.module.client;

import dagger.Binds;
import dagger.Module;
import org.example.age.common.env.EnvModule;
import org.example.age.common.env.LiteEnv;
import org.example.age.service.module.client.SiteClientRepository;

/**
 * Dagger module that binds {@link SiteClientRepository}.
 * <p>
 * Depends on an unbound...
 * <ul>
 *     <li>{@link AvsClientsConfig}
 *     <li>{@link LiteEnv}</code>
 * </ul>
 */
@Module(includes = EnvModule.class)
public interface AvsClientModule {

    @Binds
    SiteClientRepository bindSiteClientRepository(SiteClientRepositoryImpl impl);
}
