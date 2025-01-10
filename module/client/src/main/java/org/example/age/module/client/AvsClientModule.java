package org.example.age.module.client;

import dagger.Binds;
import dagger.Module;
import java.util.concurrent.ExecutorService;
import org.example.age.service.module.client.SiteClientRepository;

/**
 * Dagger module that binds {@link SiteClientRepository}.
 * <p>
 * Depends on an unbound...
 * <ul>
 *     <li>{@link AvsClientsConfig}
 *     <li><code>@Named("worker") {@link ExecutorService}</code>
 * </ul>
 */
@Module
public interface AvsClientModule {

    @Binds
    SiteClientRepository bindSiteClientRepository(SiteClientRepositoryImpl impl);
}
