package org.example.age.avs.endpoint;

import dagger.Binds;
import dagger.Module;
import org.example.age.avs.api.AvsApi;
import org.example.age.avs.spi.AgeCertificateSigner;
import org.example.age.avs.spi.AvsVerifiedUserLocalizer;
import org.example.age.avs.spi.AvsVerifiedUserStore;
import org.example.age.common.spi.AccountIdContext;
import org.example.age.common.spi.PendingStoreRepository;
import org.example.age.site.api.client.SiteApi;

/**
 * Dagger module that binds the {@link AvsApi} endpoint.
 * <p>
 * Depends on an unbound...
 * <ul>
 *     <li>{@link AccountIdContext}
 *     <li><code>Map&lt;String, {@link SiteApi}&gt;</code>
 *     <li>{@link AvsVerifiedUserStore}
 *     <li>{@link PendingStoreRepository}
 *     <li>{@link AgeCertificateSigner}
 *     <li>{@link AvsVerifiedUserLocalizer}
 *     <li>{@link AvsEndpointConfig}
 * </ul>
 */
@Module
public abstract class AvsEndpointModule {

    @Binds
    abstract AvsApi bindAvsEndpoint(AvsEndpoint endpoint);

    AvsEndpointModule() {}
}
