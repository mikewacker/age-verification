package org.example.age.service.endpoint.avs;

import dagger.Binds;
import dagger.Module;
import io.undertow.server.HttpHandler;
import org.example.age.api.def.avs.AvsApi;
import org.example.age.api.endpoint.avs.AvsApiModule;
import org.example.age.api.extractor.common.AccountIdExtractor;
import org.example.age.api.extractor.common.AuthMatchDataExtractor;
import org.example.age.service.config.avs.RefreshableAvsConfigProvider;
import org.example.age.service.config.avs.RefreshableRegisteredSiteConfigProvider;
import org.example.age.service.infra.client.RequestDispatcherModule;
import org.example.age.service.key.common.RefreshableKeyProvider;
import org.example.age.service.location.common.RefreshableSiteLocationProvider;
import org.example.age.service.store.common.PendingStoreFactory;
import org.example.age.service.store.common.VerificationStore;
import org.example.age.service.verification.internal.avs.AvsVerificationManagerModule;

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
@Module(includes = {AvsApiModule.class, AvsVerificationManagerModule.class, RequestDispatcherModule.class})
public interface AvsServiceModule {

    @Binds
    AvsApi bindAvsApi(AvsService service);
}
