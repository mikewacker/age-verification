package org.example.age.module.client;

import dagger.Binds;
import dagger.Module;
import org.example.age.avs.spi.SiteClientRepository;
import org.example.age.common.env.BaseEnvModule;
import org.example.age.common.env.LiteEnv;

/**
 * Dagger module that binds {@link SiteClientRepository}.
 * <p>
 * Depends on an unbound...
 * <ul>
 *     <li>{@link AvsClientsConfig}
 *     <li>{@link LiteEnv}</code>
 * </ul>
 */
@Module(includes = BaseEnvModule.class)
public interface AvsClientModule {

    @Binds
    SiteClientRepository bindSiteClientRepository(SiteClientRepositoryImpl impl);
}
