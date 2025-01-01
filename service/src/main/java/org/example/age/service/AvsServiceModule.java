package org.example.age.service;

import dagger.Binds;
import dagger.Module;
import jakarta.inject.Named;
import org.example.age.api.AvsApi;
import org.example.age.service.api.client.SiteClientRepository;
import org.example.age.service.api.crypto.AgeCertificateSigner;
import org.example.age.service.api.crypto.AvsVerifiedUserLocalizer;
import org.example.age.service.api.request.AccountIdExtractor;
import org.example.age.service.api.store.AvsVerifiedUserStore;
import org.example.age.service.api.store.PendingStoreRepository;

/**
 * Dagger module that binds <code>@Named("service") {@link AvsApi}</code>.
 * <p>
 * Depends on an unbound...
 * <ul>
 *     <li>{@link AccountIdExtractor}
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
