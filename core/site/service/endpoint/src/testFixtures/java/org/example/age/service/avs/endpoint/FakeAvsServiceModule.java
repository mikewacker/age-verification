package org.example.age.service.avs.endpoint;

import dagger.Binds;
import dagger.Module;
import io.undertow.server.HttpHandler;
import org.example.age.api.avs.AvsApi;
import org.example.age.api.avs.endpoint.AvsApiModule;
import org.example.age.module.extractor.common.AccountIdExtractor;
import org.example.age.module.extractor.common.AuthMatchDataExtractor;
import org.example.age.module.key.common.RefreshableKeyProvider;
import org.example.age.module.location.common.SiteLocation;
import org.example.age.service.avs.verification.internal.FakeAvsVerificationFactoryModule;
import org.example.age.service.infra.client.RequestDispatcherModule;

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
