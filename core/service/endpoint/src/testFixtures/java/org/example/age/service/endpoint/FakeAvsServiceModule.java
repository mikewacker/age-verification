package org.example.age.service.endpoint;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import io.github.mikewacker.drift.backend.BackendDispatcher;
import io.undertow.server.HttpHandler;
import jakarta.inject.Singleton;
import org.example.age.api.def.AvsApi;
import org.example.age.api.endpoint.AvsApiModule;
import org.example.age.api.extractor.AccountIdExtractor;
import org.example.age.api.extractor.AuthMatchDataExtractor;
import org.example.age.service.key.RefreshablePrivateSigningKeyProvider;
import org.example.age.service.location.RefreshableSiteLocationProvider;
import org.example.age.service.store.VerificationStore;
import org.example.age.service.verification.internal.FakeAvsVerificationFactoryModule;

/**
 * Dagger module that binds dependencies for <code>@Named("api") {@link HttpHandler}</code>.
 *
 * <p>Depends on an unbound...</p>
 * <ul>
 *     <li>{@link AccountIdExtractor}</li>
 *     <li>{@link AuthMatchDataExtractor}</li>
 *     <li>{@link VerificationStore}</li>
 *     <li>{@link RefreshablePrivateSigningKeyProvider}</li>
 *     <li>{@link RefreshableSiteLocationProvider}</li>
 * </ul>
 */
@Module(includes = {AvsApiModule.class, FakeAvsVerificationFactoryModule.class})
public interface FakeAvsServiceModule {

    @Binds
    AvsApi bindAvsApi(FakeAvsService service);

    @Provides
    @Singleton
    static BackendDispatcher provideBackendDispatcher() {
        return BackendDispatcher.create();
    }
}
