package org.example.age.avs.service.endpoint;

import dagger.Binds;
import dagger.Module;
import io.undertow.server.HttpHandler;
import org.example.age.avs.api.endpoint.AvsApi;
import org.example.age.avs.api.endpoint.AvsApiModule;
import org.example.age.avs.service.verification.internal.FakeAvsVerificationFactoryModule;
import org.example.age.infra.service.client.RequestDispatcherModule;
import org.example.age.module.config.common.SiteLocation;
import org.example.age.module.extractor.common.AccountIdExtractor;
import org.example.age.module.extractor.common.AuthMatchDataExtractor;
import org.example.age.module.key.common.RefreshableKeyProvider;

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
