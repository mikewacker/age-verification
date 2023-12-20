package org.example.age.service.endpoint.site;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import io.undertow.server.HttpHandler;
import javax.inject.Singleton;
import org.example.age.api.def.site.SiteApi;
import org.example.age.api.endpoint.site.SiteApiModule;
import org.example.age.api.extractor.common.AccountIdExtractor;
import org.example.age.api.extractor.common.AuthMatchDataExtractor;
import org.example.age.service.config.site.RefreshableSiteConfigProvider;
import org.example.age.service.infra.client.RequestDispatcher;
import org.example.age.service.key.common.RefreshableKeyProvider;
import org.example.age.service.location.common.RefreshableAvsLocationProvider;
import org.example.age.service.store.common.PendingStoreFactory;
import org.example.age.service.store.common.VerificationStore;
import org.example.age.service.verification.internal.site.SiteVerificationManagerModule;

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
@Module(includes = {SiteApiModule.class, SiteVerificationManagerModule.class})
public interface SiteServiceModule {

    @Binds
    SiteApi bindSiteApi(SiteService service);

    @Provides
    @Singleton
    static RequestDispatcher provideRequestDispatcher() {
        return RequestDispatcher.create();
    }
}
