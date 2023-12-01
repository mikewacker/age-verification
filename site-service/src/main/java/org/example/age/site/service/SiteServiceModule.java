package org.example.age.site.service;

import dagger.Binds;
import dagger.Module;
import org.example.age.common.api.extractor.AccountIdExtractor;
import org.example.age.common.api.extractor.AuthMatchDataExtractor;
import org.example.age.common.service.data.internal.DataMapperModule;
import org.example.age.common.service.store.PendingStoreFactory;
import org.example.age.infra.service.client.RequestDispatcherModule;
import org.example.age.site.api.endpoint.SiteApi;
import org.example.age.site.api.endpoint.SiteApiModule;
import org.example.age.site.service.config.SiteConfig;
import org.example.age.site.service.config.internal.SiteConfigurerModule;
import org.example.age.site.service.store.VerificationStore;
import org.example.age.site.service.verification.internal.VerificationManagerModule;

/**
 * Dagger module that binds dependencies for <code>@Named("api") HttpHandler</code>.
 *
 * <p>Depends on an unbound...</p>
 * <ul>
 *     <li>{@link AccountIdExtractor}</li>
 *     <li>{@link AuthMatchDataExtractor}</li>
 *     <li>{@link VerificationStore}</li>
 *     <li>{@link PendingStoreFactory}</li>
 *     <li>{@link SiteConfig}</li>
 * </ul>
 */
@Module(
        includes = {
            SiteApiModule.class,
            VerificationManagerModule.class,
            RequestDispatcherModule.class,
            DataMapperModule.class,
            SiteConfigurerModule.class,
        })
public interface SiteServiceModule {

    @Binds
    SiteApi bindSiteApi(SiteService service);
}
