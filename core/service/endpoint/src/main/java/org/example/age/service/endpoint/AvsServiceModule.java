package org.example.age.service.endpoint;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import io.undertow.server.HttpHandler;
import javax.inject.Singleton;
import org.example.age.api.def.AvsApi;
import org.example.age.api.endpoint.AvsApiModule;
import org.example.age.api.extractor.AccountIdExtractor;
import org.example.age.api.extractor.AuthMatchDataExtractor;
import org.example.age.service.config.RefreshableAvsConfigProvider;
import org.example.age.service.config.RefreshableRegisteredSiteConfigProvider;
import org.example.age.service.infra.client.RequestDispatcher;
import org.example.age.service.key.RefreshableKeyProvider;
import org.example.age.service.location.RefreshableSiteLocationProvider;
import org.example.age.service.store.PendingStoreFactory;
import org.example.age.service.store.VerificationStore;
import org.example.age.service.verification.internal.AvsVerificationManagerModule;

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
 *     <li>{@link RefreshableRegisteredSiteConfigProvider}</li>
 *     <li>{@link RefreshableAvsConfigProvider}</li>
 *     <li>{@link RefreshableSiteLocationProvider}</li>
 * </ul>
 */
@Module(includes = {AvsApiModule.class, AvsVerificationManagerModule.class})
public interface AvsServiceModule {

    @Binds
    AvsApi bindAvsApi(AvsService service);

    @Provides
    @Singleton
    static RequestDispatcher provideRequestDispatcher() {
        return RequestDispatcher.create();
    }
}
