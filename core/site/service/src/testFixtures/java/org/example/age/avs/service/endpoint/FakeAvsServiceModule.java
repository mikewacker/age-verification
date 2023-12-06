package org.example.age.avs.service.endpoint;

import dagger.Binds;
import dagger.Module;
import io.undertow.server.HttpHandler;
import org.example.age.avs.api.endpoint.AvsApi;
import org.example.age.avs.api.endpoint.AvsApiModule;
import org.example.age.avs.service.verification.internal.FakeAvsVerificationFactoryModule;
import org.example.age.common.api.extractor.AccountIdExtractor;
import org.example.age.common.api.extractor.AuthMatchDataExtractor;
import org.example.age.common.service.config.SiteLocation;
import org.example.age.common.service.key.RefreshableKeyProvider;
import org.example.age.infra.service.client.RequestDispatcherModule;

/**
 * Dagger module that binds dependencies for <code>@Named("api") {@link HttpHandler}</code>.
 *
 * <p>Depends on an unbound...</p>
 * <ul>
 *     <li>{@link AccountIdExtractor}</li>
 *     <li>{@link AuthMatchDataExtractor}</li>
 *     <li>{@link RefreshableKeyProvider}</li>
 *     <li><code>Provider&lt;{@link SiteLocation}&gt;</code></li>
 * </ul>
 */
@Module(includes = {AvsApiModule.class, FakeAvsVerificationFactoryModule.class, RequestDispatcherModule.class})
public interface FakeAvsServiceModule {

    @Binds
    AvsApi bindAvsApi(FakeAvsService service);
}
