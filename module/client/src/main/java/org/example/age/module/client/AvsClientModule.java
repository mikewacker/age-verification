package org.example.age.module.client;

import dagger.Binds;
import dagger.Module;
import org.example.age.module.common.CommonModule;
import org.example.age.module.common.LiteEnv;
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
@Module(includes = CommonModule.class)
public interface AvsClientModule {

    @Binds
    SiteClientRepository bindSiteClientRepository(SiteClientRepositoryImpl impl);
}
