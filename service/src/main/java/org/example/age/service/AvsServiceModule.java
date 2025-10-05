package org.example.age.service;

import dagger.Binds;
import dagger.Module;
import jakarta.inject.Named;
import org.example.age.avs.api.AvsApi;
import org.example.age.avs.spi.AgeCertificateSigner;
import org.example.age.avs.spi.AvsVerifiedUserLocalizer;
import org.example.age.avs.spi.AvsVerifiedUserStore;
import org.example.age.common.spi.AccountIdContext;
import org.example.age.common.spi.PendingStoreRepository;
import org.example.age.site.api.client.SiteApi;

/**
 * Dagger module that binds <code>@Named("service") {@link AvsApi}</code>.
 * <p>
 * Depends on an unbound...
 * <ul>
 *     <li>{@link AccountIdContext}
 *     <li><code>Map&lt;String, {@link SiteApi}&gt;</code>
 *     <li>{@link AvsVerifiedUserStore}
 *     <li>{@link PendingStoreRepository}
 *     <li>{@link AgeCertificateSigner}
 *     <li>{@link AvsVerifiedUserLocalizer}
 *     <li>{@link AvsServiceConfig}
 * </ul>
 */
@Module
public interface AvsServiceModule {

    @Binds
    @Named("service")
    AvsApi bindAvsService(AvsService service);
}
