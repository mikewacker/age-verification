package org.example.age.service.site.endpoint;

import dagger.Binds;
import dagger.Module;
import io.undertow.server.HttpHandler;
import org.example.age.api.def.site.SiteApi;
import org.example.age.api.endpoint.site.SiteApiModule;
import org.example.age.api.module.extractor.common.AccountIdExtractor;
import org.example.age.api.module.extractor.common.AuthMatchDataExtractor;
import org.example.age.module.config.site.RefreshableSiteConfigProvider;
import org.example.age.module.key.common.RefreshableKeyProvider;
import org.example.age.module.location.common.RefreshableAvsLocationProvider;
import org.example.age.module.store.common.PendingStoreFactory;
import org.example.age.module.store.common.VerificationStore;
import org.example.age.service.infra.client.RequestDispatcherModule;
import org.example.age.service.site.verification.internal.SiteVerificationManagerModule;

/**
 * Dagger module that binds dependencies for <code>@Named("api") {@link HttpHandler}</code>.
 *
 * <p>Depends on an unbound...</p>
 * <ul>
 *     <li>{@link AccountIdExtractor}</li>
 *     <li>{@link AuthMatchDataExtractor}</li>
 *     <li>{@link VerificationStore}</li>
 *     <li>{@link PendingStoreFactory}</li>
 *     <li>{@link RefreshableKeyProvider}</li>
 *     <li>{@link RefreshableSiteConfigProvider}</li>
 *     <li>{@link RefreshableAvsLocationProvider}</li>
 * </ul>
 */
@Module(includes = {SiteApiModule.class, SiteVerificationManagerModule.class, RequestDispatcherModule.class})
public interface SiteServiceModule {

    @Binds
    SiteApi bindSiteApi(SiteService service);
}
