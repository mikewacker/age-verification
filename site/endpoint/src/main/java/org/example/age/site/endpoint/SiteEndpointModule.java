package org.example.age.site.endpoint;

import dagger.Binds;
import dagger.Module;
import org.example.age.avs.api.client.AvsApi;
import org.example.age.common.spi.AccountIdContext;
import org.example.age.common.spi.PendingStoreRepository;
import org.example.age.site.api.SiteApi;
import org.example.age.site.spi.AgeCertificateVerifier;
import org.example.age.site.spi.SiteVerifiedAccountStore;
import org.example.age.site.spi.SiteVerifiedUserLocalizer;

/**
 * Dagger module that binds the {@link SiteApi} endpoint.
 * <p>
 * Depends on an unbound...
 * <ul>
 *     <li>{@link AccountIdContext}
 *     <li>{@link AvsApi}
 *     <li>{@link SiteVerifiedAccountStore}
 *     <li>{@link PendingStoreRepository}
 *     <li>{@link AgeCertificateVerifier}
 *     <li>{@link SiteVerifiedUserLocalizer}
 *     <li>{@link SiteEndpointConfig}
 * </ul>
 */
@Module
public abstract class SiteEndpointModule {

    @Binds
    abstract SiteApi bindSiteEndpoint(SiteEndpoint endpoint);

    SiteEndpointModule() {}
}
