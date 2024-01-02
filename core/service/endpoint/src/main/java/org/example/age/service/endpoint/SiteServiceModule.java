package org.example.age.service.endpoint;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import io.github.mikewacker.drift.backend.BackendDispatcher;
import io.undertow.server.HttpHandler;
import javax.inject.Singleton;
import org.example.age.api.def.SiteApi;
import org.example.age.api.endpoint.SiteApiModule;
import org.example.age.api.extractor.AccountIdExtractor;
import org.example.age.api.extractor.AuthMatchDataExtractor;
import org.example.age.service.config.RefreshableSiteConfigProvider;
import org.example.age.service.key.RefreshablePseudonymKeyProvider;
import org.example.age.service.key.RefreshablePublicSigningKeyProvider;
import org.example.age.service.location.RefreshableAvsLocationProvider;
import org.example.age.service.store.PendingStoreFactory;
import org.example.age.service.store.VerificationStore;
import org.example.age.service.verification.internal.SiteVerificationManagerModule;

/**
 * Dagger module that binds dependencies for <code>@Named("api") {@link HttpHandler}</code>.
 *
 * <p>Depends on an unbound...</p>
 * <ul>
 *     <li>{@link AccountIdExtractor}</li>
 *     <li>{@link AuthMatchDataExtractor}</li>
 *     <li>{@link VerificationStore}</li>
 *     <li>{@link PendingStoreFactory}</li>
 *     <li>{@link RefreshablePublicSigningKeyProvider}</li>
 *     <li>{@link RefreshablePseudonymKeyProvider}</li>
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
    static BackendDispatcher provideBackendDispatcher() {
        return BackendDispatcher.create();
    }
}
