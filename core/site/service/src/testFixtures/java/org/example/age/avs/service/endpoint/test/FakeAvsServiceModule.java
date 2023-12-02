package org.example.age.avs.service.endpoint.test;

import dagger.Binds;
import dagger.Module;
import org.example.age.avs.api.endpoint.AvsApi;
import org.example.age.avs.api.endpoint.AvsApiModule;
import org.example.age.avs.service.verification.internal.FakeAvsVerificationFactoryModule;
import org.example.age.common.api.extractor.builtin.DisabledAuthMatchDataExtractorModule;
import org.example.age.common.api.extractor.test.TestAccountIdExtractorModule;
import org.example.age.common.service.config.test.TestSiteLocationModule;
import org.example.age.infra.service.client.RequestDispatcherModule;
import org.example.age.testing.server.TestServer;

/**
 * Dagger module that binds dependencies for <code>@Named("api") HttpHandler</code>.
 *
 * <p>Depends on an unbound <code>@Named("site") {@link TestServer}&lt?&gt;</code>.</p>
 */
@Module(
        includes = {
            AvsApiModule.class,
            TestAccountIdExtractorModule.class,
            DisabledAuthMatchDataExtractorModule.class,
            FakeAvsVerificationFactoryModule.class,
            RequestDispatcherModule.class,
            TestSiteLocationModule.class,
        })
public interface FakeAvsServiceModule {

    @Binds
    AvsApi bindAvsApi(FakeAvsService service);
}
