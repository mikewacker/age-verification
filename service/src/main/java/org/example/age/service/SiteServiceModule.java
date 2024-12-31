package org.example.age.service;

import dagger.Binds;
import dagger.Module;
import jakarta.inject.Named;
import org.example.age.api.SiteApi;
import org.example.age.api.client.AvsApi;
import org.example.age.service.api.AccountIdExtractor;
import org.example.age.service.api.AgeCertificateVerifier;
import org.example.age.service.api.PendingStoreRepository;
import org.example.age.service.api.SiteLocalizationKeyStore;
import org.example.age.service.api.SiteVerificationStore;

/**
 * Dagger module that binds <code>@Named("service") {@link SiteApi}</code>.
 * <p>
 * Depends on an unbound...
 * <ul>
 *     <li>{@link AccountIdExtractor}
 *     <li><code>@Named("client") {@link AvsApi}</code>
 *     <li>{@link SiteVerificationStore}
 *     <li>{@link PendingStoreRepository}
 *     <li>{@link AgeCertificateVerifier}
 *     <li>{@link SiteLocalizationKeyStore}
 *     <li>{@link SiteServiceConfig}
 * </ul>
 */
@Module
public interface SiteServiceModule {

    @Binds
    @Named("service")
    SiteApi bindSiteService(SiteService service);
}
