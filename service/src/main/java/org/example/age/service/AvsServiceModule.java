package org.example.age.service;

import dagger.Binds;
import dagger.Module;
import jakarta.inject.Named;
import org.example.age.avs.api.AvsApi;
import org.example.age.common.spi.AccountIdContext;
import org.example.age.common.spi.PendingStoreRepository;
import org.example.age.service.module.client.SiteClientRepository;
import org.example.age.service.module.crypto.AgeCertificateSigner;
import org.example.age.service.module.crypto.AvsVerifiedUserLocalizer;
import org.example.age.service.module.store.AvsVerifiedUserStore;

/**
 * Dagger module that binds <code>@Named("service") {@link AvsApi}</code>.
 * <p>
 * Depends on an unbound...
 * <ul>
 *     <li>{@link AccountIdContext}
 *     <li>{@link SiteClientRepository}
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
