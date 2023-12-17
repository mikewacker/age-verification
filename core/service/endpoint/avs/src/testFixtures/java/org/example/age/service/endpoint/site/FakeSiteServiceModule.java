package org.example.age.service.endpoint.site;

import dagger.Binds;
import dagger.Module;
import io.undertow.server.HttpHandler;
import org.example.age.api.def.site.SiteApi;
import org.example.age.api.endpoint.site.SiteApiModule;
import org.example.age.api.module.extractor.common.AccountIdExtractor;
import org.example.age.api.module.extractor.common.AuthMatchDataExtractor;
import org.example.age.module.location.common.test.TestAvsLocationModule;
import org.example.age.service.infra.client.RequestDispatcherModule;
import org.example.age.service.module.key.common.RefreshableKeyProvider;
import org.example.age.service.module.location.common.RefreshableAvsLocationProvider;
import org.example.age.service.store.common.VerificationStore;
import org.example.age.service.verification.internal.site.FakeSiteVerificationProcessorModule;

/**
 * Dagger module that binds dependencies for <code>@Named("api") {@link HttpHandler}</code>.
 *
 * <p>Depends on an unbound...</p>
 * <ul>
 *     <li>{@link AccountIdExtractor}</li>
 *     <li>{@link AuthMatchDataExtractor}</li>
 *     <li>{@link VerificationStore}</li>
 *     <li>{@link RefreshableKeyProvider}</li>
 *     <li>{@link RefreshableAvsLocationProvider}</li>
 * </ul>
 */
@Module(
        includes = {
            SiteApiModule.class,
            FakeSiteVerificationProcessorModule.class,
            TestAvsLocationModule.class,
            RequestDispatcherModule.class,
        })
public interface FakeSiteServiceModule {

    @Binds
    SiteApi bindAvsApi(FakeSiteService service);
}
