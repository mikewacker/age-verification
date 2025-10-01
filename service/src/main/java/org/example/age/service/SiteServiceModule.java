package org.example.age.service;

import dagger.Binds;
import dagger.Module;
import jakarta.inject.Named;
import org.example.age.common.spi.request.AccountIdContext;
import org.example.age.service.module.crypto.AgeCertificateVerifier;
import org.example.age.service.module.crypto.SiteVerifiedUserLocalizer;
import org.example.age.service.module.store.PendingStoreRepository;
import org.example.age.service.module.store.SiteVerificationStore;
import org.example.age.site.api.SiteApi;

/**
 * Dagger module that binds <code>@Named("service") {@link SiteApi}</code>.
 * <p>
 * Depends on an unbound...
 * <ul>
 *     <li>{@link AccountIdContext}
 *     <li><code>@Named("client") {@link AvsApi}</code>
 *     <li>{@link SiteVerificationStore}
 *     <li>{@link PendingStoreRepository}
 *     <li>{@link AgeCertificateVerifier}
 *     <li>{@link SiteVerifiedUserLocalizer}
 *     <li>{@link SiteServiceConfig}
 * </ul>
 */
@Module
public interface SiteServiceModule {

    @Binds
    @Named("service")
    SiteApi bindSiteService(SiteService service);
}
