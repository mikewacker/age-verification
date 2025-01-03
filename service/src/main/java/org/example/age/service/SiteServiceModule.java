package org.example.age.service;

import dagger.Binds;
import dagger.Module;
import jakarta.inject.Named;
import org.example.age.api.SiteApi;
import org.example.age.api.client.AvsApi;
import org.example.age.service.api.crypto.AgeCertificateVerifier;
import org.example.age.service.api.crypto.SiteVerifiedUserLocalizer;
import org.example.age.service.api.request.AccountIdContext;
import org.example.age.service.api.store.PendingStoreRepository;
import org.example.age.service.api.store.SiteVerificationStore;

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
