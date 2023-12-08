package org.example.age.site.service.endpoint;

import dagger.Binds;
import dagger.Module;
import io.undertow.server.HttpHandler;
import org.example.age.infra.service.client.RequestDispatcherModule;
import org.example.age.module.config.site.SiteConfig;
import org.example.age.module.extractor.common.AccountIdExtractor;
import org.example.age.module.extractor.common.AuthMatchDataExtractor;
import org.example.age.module.key.common.RefreshableKeyProvider;
import org.example.age.module.store.common.PendingStoreFactory;
import org.example.age.module.store.common.VerificationStore;
import org.example.age.site.api.endpoint.SiteApi;
import org.example.age.site.api.endpoint.SiteApiModule;
import org.example.age.site.service.verification.internal.SiteVerificationManagerModule;

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
 *     <li><code>Provider&lt;{@link SiteConfig}&gt;</code></li>
 * </ul>
 */
@Module(includes = {SiteApiModule.class, SiteVerificationManagerModule.class, RequestDispatcherModule.class})
public interface SiteServiceModule {

    @Binds
    SiteApi bindSiteApi(SiteService service);
}
